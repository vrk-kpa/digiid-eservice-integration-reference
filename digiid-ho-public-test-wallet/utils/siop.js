import * as jose from 'jose';
import jsonld from 'jsonld';
import crypto from 'crypto';

const generateAuthData = async (
  privateKey,
  keyId,
  coreId,
  nonce,
  clientId,
  siopPresentationDefinition,
) => {
  const subject = coreId.proof.domain;

  const id_token = {
    iss: 'https://self-issued.me/v2',
    aud: clientId,
    iat: Date.now(),
    exp: Date.now() + 3 * 60 * 1000,
    sub: subject,
    auth_time: Date.now(),
    nonce,
    _vp_token: {
      presentation_submission: {
        id: '9d7a64a8-87ec-11ec-9fd1-93f208b40a93',
        definition_id: '935f5f5e-87ed-11ec-bb15-f351e950dffd',
        descriptor_map: [
          {
            id: 'ID Card with constraints',
            format: 'ldp_vp',
            path: '$.verifiableCredential[0]',
          },
        ],
      },
    },
  };

  const now = new Date().toISOString();

  const requestedCredentials = () => {
    const credentials = JSON.parse(JSON.stringify(coreId));
    if (siopPresentationDefinition) {
      const presentationDefinitionIds = siopPresentationDefinition['input_descriptors'].map((id) => id['id']);
      credentials['verifiableCredential'] = coreId['verifiableCredential'].filter((vc) => (!!presentationDefinitionIds.includes(vc['type'][2])));
    }
    return credentials;
  };

  const vp_token = {
    ...requestedCredentials(),
    holder: subject, // "did:example:holder",
  };
  vp_token.proof = undefined;

  vp_token['@context'] = [
    'https://www.w3.org/2018/credentials/v1',
    'https://w3id.org/security/suites/jws-2020/v1',
  ];

  const canonized = await jsonld.canonize(vp_token, {
    algorithm: 'URDNA2015',
  });

  const proof = {
    '@context': [
      'https://www.w3.org/2018/credentials/v1',
      'https://w3id.org/security/suites/jws-2020/v1',
    ],
    type: 'JsonWebSignature2020',
    created: `${now.substring(0, now.lastIndexOf('.'))}Z`,
    proofPurpose: 'authentication',
    verificationMethod: keyId, // "did:example:holder#key-1"
    challenge: nonce,
    domain: clientId,
  };

  const canonizedProof = await jsonld.canonize(proof, {
    algorithm: 'URDNA2015',
  });

  const key = await jose.importPKCS8(privateKey, 'ES256');

  const hash = crypto.createHash('sha256').update(canonized).digest('hex');

  const hashProof = crypto.createHash('sha256').update(canonizedProof).digest('hex');

  const header = { b64: false, crit: ['b64'], alg: 'ES256' };

  const payload = Buffer.from(hashProof + hash, 'hex');

  const jws = await new jose.CompactSign(payload)
    .setProtectedHeader(header)
    .sign(key);

  // Delete context from proof before assigning it to vp_token
  delete proof['@context'];

  vp_token.proof = proof;

  vp_token.proof.jws = jws;

  const idJwt = await new jose.CompactSign(
    new TextEncoder('utf-8').encode(JSON.stringify(id_token)),
  )
    .setProtectedHeader({ alg: 'ES256' })
    .sign(key);

  return { id_token: idJwt, vp_token };
};

export default generateAuthData;
