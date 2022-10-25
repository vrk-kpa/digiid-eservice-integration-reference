import React, {FC} from 'react';
import { Box, Button, Chip, Stack, TextField, Tooltip } from '@mui/material';
import { QuestionMark } from '@mui/icons-material';
import defaultData from '../../pages/defaultData.json';
import { useDigiWallet } from '../DigiWalletProvider';
import generateAuthData from '../../utils/siop';
import digiidCore from '../../pages/digiidCore.json';
import UsageInstructionsAccordion from './UsageInstructionsAccordion';
import AccordionBase from './AccordionBase';


type Props = {
  coreId: object,
};

type SIOPAuthData = {
  vp_token: string,
  id_token: string,
};

type SIOPPresentationDefinition = {
  id: string,
  input_descriptors: object[],
};

const SIOPAccordion: FC<Props> = ({ coreId }: Props) => {
  const {
    getPrivateKey,
    getCert,
  } = useDigiWallet();

  const [siopLoginResp, setSiopLoginResp] = React.useState<string>('');
  const [siopLoginScopes, setSiopLoginScopes] = React.useState<string[]>([]);
  const [siopLoginClaims, setSiopLoginClaims] = React.useState<string[]>([]);
  const [
    siopPresentationDefinition,
    setSiopPresentationDefinition] = React.useState<SIOPPresentationDefinition | string>(null);
  const [siopAuthData, setSiopAuthData] = React.useState<SIOPAuthData>(null);
  const [siopAuthResp, setSiopAuthResp] = React.useState<string>('');
  const [siopCheckResp, setSiopCheckResp] = React.useState<string>('');
  const [siopCancellationResp, setSiopCancellationResp] = React.useState<string>('');

  const generateSiopAuthData = async () => {
    try {
      const { searchParams } = new URL(siopLoginResp);
      const data = await generateAuthData(
        getPrivateKey(),
        getCert().keyId,
        coreId,
        searchParams.get('nonce'),
        searchParams.get('client_id'),
        siopPresentationDefinition,
      );
      setSiopAuthData(data);
    } catch (error) {
      setSiopAuthData(error);
      console.log('ASD:', error);
    }
  };

  const sendSiopLogin = async () => {
    try {
      const resp = await fetch(`${defaultData.backendSiop}/login`, {
        method: 'POST',
        mode: 'cors',
        headers: {
          'Content-Type': 'application/json;charset=utf-8',
        },
        body: JSON.stringify({
          nonce: crypto.randomUUID(),
          scopes: siopLoginScopes,
          claims: siopLoginClaims,
        }),
      });

      const data = await resp.text();
      setSiopLoginResp(data);
      await fetchSiopPresentationDefinition(data);
    } catch (error) {
      if (error.response) {
        setSiopLoginResp(
          `ERROR:${error.response.status} : ${error.response.data}`,
        );
      } else if (error.request) {
        setSiopLoginResp(error.request);
      } else {
        setSiopLoginResp(`ERROR:${error.message}`);
      }
    }
  };

  const fetchSiopPresentationDefinition = async (data: string) => {
    const urlParams = new URL(data).searchParams;
    const claims = urlParams.get('claims');

    if (urlParams.get('scope').includes('digiid_core')) {
      setSiopPresentationDefinition(digiidCore);
      return;
    }

    if (claims?.includes('presentation_definition_uri')) {
      const uri = JSON.parse(claims).vp_token?.presentation_definition_uri;
      if (!uri) {
        setSiopPresentationDefinition('ERROR: presentation definition uri not set');
        return;
      }
      try {
        const resp = await fetch(uri);
        const presDef = await resp.json();
        if (resp.status !== 200) {
          setSiopPresentationDefinition(`ERROR:${data}`);
          return;
        }
        setSiopPresentationDefinition(presDef);
        setSiopLoginClaims(presDef['input_descriptors']?.map((c) => c['id']));
      } catch (error) {
        if (error.response) {
          setSiopPresentationDefinition(
            `ERROR:${error.response.status} : ${error.response.data}`,
          );
        } else if (error.request) {
          setSiopPresentationDefinition(error.request);
        } else {
          setSiopPresentationDefinition(`ERROR:${error.message}`);
        }
      }
    }
  };

  const sendSiopAuth = async () => {
    try {
      if (!siopAuthData?.id_token || !siopAuthData?.vp_token) {
        setSiopAuthResp(
          'ERROR:id_token or vp_token missing',
        );
        return;
      }
      const form = new URLSearchParams({
        id_token: siopAuthData.id_token,
        vp_token: JSON.stringify(siopAuthData.vp_token),
      });

      const resp = await fetch(`${defaultData.backendSiop}/auth`, {
        method: 'POST',
        mode: 'cors',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: form,
      });

      const data = await resp.text();
      setSiopAuthResp(data);
    } catch (error) {
      if (error.response) {
        setSiopAuthResp(
          `ERROR:${error.response.status} : ${error.response.data}`,
        );
      } else if (error.request) {
        setSiopAuthResp(error.request);
      } else {
        setSiopAuthResp(`ERROR:${error.message}`);
      }
    }
  };

  const sendSiopCheck = async () => {
    try {
      setSiopCheckResp('');

      const url = new URL(siopLoginResp);
      const nonce = url.searchParams.get('nonce');

      const resp = await fetch(`${defaultData.backendSiop}/status/${nonce}`, {
        method: 'GET',
        mode: 'cors',
        headers: {
          'Content-Type': 'application/json;charset=utf-8',
        },
      });
      setSiopCheckResp(await resp.json());
    } catch (error) {
      if (error.response) {
        setSiopCheckResp(
          `ERROR:${error.response.status} : ${error.response.data}`,
        );
      } else if (error.request) {
        setSiopCheckResp(error.request);
      } else {
        setSiopCheckResp(`ERROR:${error.message}`);
      }
    }
  };

  const sendSiopCancellation = async () => {
    try {
      const form = new URLSearchParams({
        error: 'user_cancelled',
        state: new URL(siopLoginResp).searchParams.get('nonce'),
      });

      const resp = await fetch(`${defaultData.backendSiop}/auth`, {
        method: 'POST',
        mode: 'cors',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: form,
      });

      const data = await resp.json();
      setSiopCancellationResp(data);
    } catch (error) {
      if (error.response) {
        setSiopCancellationResp(
          `ERROR:${error.response.status} : ${error.response.data}`,
        );
      } else if (error.request) {
        setSiopCancellationResp(error.request);
      } else {
        setSiopCancellationResp(`ERROR:${error.message}`);
      }
    }
  };

  const digiidCoreChipVariant = siopLoginScopes?.includes('digiid_core') ? 'filled' : 'outlined';
  return (
    <AccordionBase title="SIOP">
      <UsageInstructionsAccordion>
        <Box display="flex" flexDirection="column" m={1}>
          Used to send verifiable credentials via SIOP.
          Supports both direct SIOP and the OIDC adapter (via copypaste to the login-field).
          Remember to fetch CoreID first,
          because the SIOP authentication
          is constructed using the fetched VerifiableCredentials.
          <div>The SIOP flow can be tested in the following way:</div>
          <ol>
            <li><b>Fetch coreID using the previous accordion</b></li>
            <li>Local backend is used in test</li>
            <li>
              Generate a login url by either:
              <ul>
                <li>
                  Selecting the wanted scope or
                  claims manually and by pressing the &quot;send login&quot; button
                </li>
                <li>
                  Copypasting the QR code link to the &quot;login response&quot;
                  field from op-ui/siop-ui and
                  {' '}
                  <i>not</i>
                  {' '}
                  pressing the &quot;send login&quot; button
                </li>
              </ul>
            </li>
            <li>
              The presentation definition is automatically
              fetched and displayed, if it exists
            </li>
            <li>Generate the vp_token and id_token by pressing  &quot;Generate auth&quot;</li>
            <li>Send the tokens to the backend by pressing &quot;Send auth&quot;</li>
            <li>
              View the credentials by pressing
              &quot;Check auth status&quot; once the auth is sent.
            </li>
          </ol>
        </Box>
      </UsageInstructionsAccordion>
      <Box display="flex" flexDirection="column" m={1}>
        <Box display="flex" flexDirection="row" m={1}>
          <Stack direction="row" spacing={1} mr={25}>
            <Chip label="Scopes" color="primary" />
            {coreId ? (
              <Chip
                label="digiid_core"
                disabled={siopLoginClaims && siopLoginClaims.length !== 0}
                variant={digiidCoreChipVariant}
                onClick={() => {
                  if (siopLoginScopes?.includes('digiid_core')) {
                    setSiopLoginScopes([...siopLoginScopes].filter((scope) => scope !== 'digiid_core'));
                  } else {
                    setSiopLoginScopes([...siopLoginScopes, 'digiid_core']);
                  }
                }}
              />
            ) : (
              <Tooltip title="Scope selection. Will be populated once coreId is fetched.">
                <Chip label={<QuestionMark />} />
              </Tooltip>
            )}
          </Stack>
          <Stack direction="row" spacing={1} style={{ flexWrap: 'wrap', gap: '4px' }}>
            <Chip label="Claims" color="primary" style={{ marginLeft: '8px' }}/>
            {coreId ? (
              coreId['verifiableCredential'].map((claim) => {
                const claimId = claim['type'][2];
                if (claimId === 'portrait') {
                  // portrait cannot be sent via siop authentication because of POHA requirements
                  return null;
                }
                const selected = siopLoginClaims?.includes(claimId);
                return (
                  <Chip
                    key={claimId}
                    label={claimId}
                    variant={selected ? 'filled' : 'outlined'}
                    disabled={siopLoginScopes && siopLoginScopes.length !== 0}
                    onClick={() => {
                      if (selected) {
                        setSiopLoginClaims([...siopLoginClaims]
                          .filter((claimCandidate) => claimCandidate !== claimId));
                      } else {
                        setSiopLoginClaims([...siopLoginClaims, claimId]);
                      }
                    }}
                  />
                );
              })) : (
                <Tooltip title="Claims selection. Will be populated once coreId is fetched.">
                  <Chip label={<QuestionMark />} />
                </Tooltip>
            )}
          </Stack>
        </Box>
        <Box display="flex" flexDirection="row" m={1}>
          <Button
            variant="contained"
            onClick={() => sendSiopLogin()}
            sx={{ maxHeight: 40 }}
          >
            Send login
          </Button>
          <TextField
            InputLabelProps={{ shrink: true }}
            sx={{ m: 1, flexGrow: 1 }}
            label="Login response"
            multiline
            rows={10}
            onChange={async (e) => {
              setSiopLoginResp(e.target.value);
              await fetchSiopPresentationDefinition(e.target.value);
              if (e.target.value) {
                const url = new URL(e.target.value);
                const scopes = url.searchParams.get('scope')?.split(' ');
                setSiopLoginScopes(scopes.filter((scope) => scope !== 'openid'));
                const claimsParam = url.searchParams.get('claims');
                if (claimsParam) {
                  const parsed = JSON.parse(claimsParam);
                  if (parsed['vp_token']['presentation_definition']) {
                    const claims = parsed['vp_token'][
                      'presentation_definition'
                    ]['input_descriptors']?.map((c) => c['id']);
                    setSiopLoginClaims(claims);
                  }
                } else {
                  setSiopLoginClaims([]);
                }
              } else {
                setSiopLoginClaims([]);
              }
            }}
            value={siopLoginResp}
          />
          <TextField
            InputLabelProps={{ shrink: true }}
            sx={{ m: 1, flexGrow: 1 }}
            label="Presentation definition"
            multiline
            disabled
            rows={10}
            value={siopPresentationDefinition ? JSON.stringify(siopPresentationDefinition, null, 2) : ''}
          />
          <Button
            variant="contained"
            onClick={() => generateSiopAuthData()}
            sx={{ maxHeight: 40 }}
          >
            Generate auth
          </Button>
          <TextField
            InputLabelProps={{ shrink: true }}
            sx={{ m: 1, flexGrow: 1 }}
            label="Auth data"
            multiline
            disabled
            rows={10}
            value={siopAuthData ? JSON.stringify(siopAuthData, null, 2) : ''}
          />
        </Box>
        <Box display="flex" flexDirection="row" m={1}>
          <Button
            variant="contained"
            onClick={() => sendSiopAuth()}
            sx={{ maxHeight: 40 }}
          >
            Send auth
          </Button>
          <TextField
            InputLabelProps={{ shrink: true }}
            sx={{ m: 1, flexGrow: 1 }}
            label="Auth response"
            multiline
            disabled
            rows={4}
            value={siopAuthResp}
          />
          <Button
            variant="contained"
            onClick={() => sendSiopCheck()}
            sx={{ maxHeight: 40 }}
          >
            Check auth status
          </Button>
          <TextField
            InputLabelProps={{ shrink: true }}
            sx={{ m: 1, flexGrow: 1 }}
            label="Auth status"
            multiline
            disabled
            rows={4}
            value={JSON.stringify(siopCheckResp, null, 2)}
          />
          <Button
            variant="contained"
            onClick={() => sendSiopCancellation()}
            sx={{ maxHeight: 40 }}
          >
            Cancel transaction
          </Button>
          <TextField
            InputLabelProps={{ shrink: true }}
            sx={{ m: 1, flexGrow: 1 }}
            label="Cancel response"
            multiline
            disabled
            rows={4}
            value={JSON.stringify(siopCancellationResp, null, 2)}
          />
        </Box>
      </Box>
    </AccordionBase>
  );
};

export default SIOPAccordion;
