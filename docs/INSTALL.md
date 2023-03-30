# Installation instructions

`git`, `openssl` and Docker Compose are needed to run this reference implementation.

Installation steps:
1. Install `git`, `openssl` and Docker Compose.
2. Download the git repository to your machine.
3. Generate private keys and certificates.
4. Build and run applications with Docker Compose.

## Download the git repository

Download the git repository to your machine and change into its repository:

```sh
git clone https://github.com/vrk-kpa/digiid-eservice-integration-reference.git
cd digiid-eservice-integration-reference
```

## Generate private keys and certificates

1. Generate a private key.

```sh
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:4096 -outform PEM -out digiid-ho-public-oidc-tester/client-private-key.pem
```

2. Generate a certificate. Certificate generation command requires the private key generated in the previous step as input.

```sh
openssl req -batch -new -x509 -sha256 -days 365 -key digiid-ho-public-oidc-tester/client-private-key.pem -outform PEM -out digiid-ho-public-oidc-tester/client-certificate.pem
```

Copy the **contents of this certificate file** into a section `fi.dvv.digiid.op.oidcClients.publicKey` (marked with *TODO add certificate*) in file: digiid-ho-public/digiid-ho-public-op/rest/src/main/resources/application.yml

Make sure to indent the whole certificate to the same level as `-----BEGIN CERTIFICATE-----` line.

For example, this section:

```yaml
fi.dvv.digiid.op:
  oidcClients:
    - clientId: localhost
      redirectUri: http://localhost:3030/auth/callback
      publicKey: |
        -----BEGIN CERTIFICATE-----
        TODO Add certificate
        -----END CERTIFICATE-----
```

should look like this after the certificate has been added (more lines though):

```yaml
fi.dvv.digiid.op:
  oidcClients:
    - clientId: localhost
      redirectUri: http://localhost:3030/auth/callback
      publicKey: |
        -----BEGIN CERTIFICATE-----
        MIIFazCCA1OgAwIBAgIUFt8Qo2ejogvoNv3lKadJdQfDO4IwDQYJKoZIhvcNAQEL
        BQAwRTELMAkGA1UEBhMCQVUxEzARBgNVBAgMClNvbWUtU3RhdGUxITAfBgNVBAoM
        GEludGVybmV0IFdpZGdpdHMgUHR5IEx0ZDAeFw0yMjEwMjEwNTU3NDlaFw0yMzEw
        MjEwNTU3NDlaMEUxCzAJBgNVBAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEw
        ...
        ...
        -----END CERTIFICATE-----
```

3. Generate a signing key.

```sh
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:4096 -outform PEM -out digiid-ho-public/digiid-ho-public-op/rest/src/main/resources/signing-private-key.pem
```

4. Generate a signing certificate.

```sh
openssl req -batch -new -x509 -sha256 -days 365 -key digiid-ho-public/digiid-ho-public-op/rest/src/main/resources/signing-private-key.pem -outform PEM -out digiid-ho-public/digiid-ho-public-op/rest/src/main/resources/signing-certificate.pem
```

## Build and run applications with Docker Compose

Build images and start Docker containers by running Docker Compose.

```sh
docker compose up
```

Note that the Docker containers currently connect to the `host` network. The security implications of this are known by DVV's development team. This might be fixed in the future.

## Usage

For usage instructions see [usage](USAGE.md) instructions.
