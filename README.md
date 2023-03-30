# digiid-eservice-integration-reference

## ℹ️ Code development halted as of 2023-03-31
Code development in this repository has been halted indefinitely due to there not being enough time in the current session of the Finnish Parliament for passing the necessary digital identity legislation. [Read more](https://dvv.fi/-/digitaalisen-henkilollisyyden-uudistus-keskeytyi?languageId=en_US).

## Description

In this repository [DVV](https://dvv.fi) (Finnish Digital and Population Data Services Agency) publishes [Digi-ID](https://wiki.dvv.fi/x/Zrc_CQ) e-service integration reference code. The published code is intended as a reference for stakeholder developers. The reference code aims to help these parties better understand the technical implementation and the authentication flows of the Digi-ID system. Another goal is to accelerate the development of authentication solutions that integrate with the Suomi.fi Wallet mobile application.

DVV maintains the code according to its own schedule. The code is not intended for production use. The implementation, interfaces and other solutions in this repository do not necessarily align with the implementations in DVV's test and production environments. Please, note that the Self-Issued OpenID Provider v2 specification and other specifications that this reference is based on are not yet finalised. It is possible that some changes might occur before Suomi.fi Wallet is published into production use. Some features, such as the management of relying parties, are still in the development pipeline and are not included in this reference.

The published code is open source and licensed with the MIT license.

The repository contains digital identity e-service SIOPv2 reference application components and a SIOPv2-OIDC adapter. Components include:
- component simulating a digital identity wallet (including a fixed test identity)
- component simulating a relying party
- component for authentication
- streamlined component providing a Verifiable Data Registry service

## About security and third party code

The code in this repository depends on third party code. To address security concerns code compilation and execution is performed inside Docker containers. Although such measures have been implemented, DVV will not take responsibility of any security issues in the source code or in the third party components.

## Getting started

Please see [installation](docs/INSTALL.md) and [usage](docs/USAGE.md) instructions.

## Further information and documentation

[Digital identity reform](https://dvv.fi/en/digital-identity-reform)

[Digital identity development project](https://wiki.dvv.fi/x/Zrc_CQ)

[SIOPv2 POC - Guide for Relying Parties](https://wiki.dvv.fi/x/CoqVCg)

## Known issues

- Docker containers currently connect to the host network. The security implications of this are known by DVV's development team. This might be fixed in the future.
- Mac OS has known issues with Docker's host network. As a workaround the reference implementation can be started locally by running Dockerfile commads.

## Contact

[digihenkilollisyys@dvv.fi](mailto:digihenkilollisyys@dvv.fi)

## Copyright and licensing

See [LICENSE.md](LICENSE.md) and [LICENSE-verify-with-nodejs.md](LICENSE-verify-with-nodejs.md).
