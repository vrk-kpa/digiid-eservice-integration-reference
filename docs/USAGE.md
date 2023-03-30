# Usage instructions

The reference implementation should be running in your local environment before these instructions can be followed. See [installation](INSTALL.md) instructions on how to get it running.

This documents explains how you can test SIOP and OIDC-SIOP authentication flows or check the VDR interface. Additional instructions can be found in the test wallet UI ("Usage instructions" accordions) which can accessed by visiting http://localhost:3300.

## SIOP authentication flow

Follow these instructions to test the SIOP authentication flow.

Open the test wallet UI's address http://localhost:3300 in your browser.

### Fetch test identity

Open the "CoreId" accordion. Fetch a test identity from a local file with "FETCH COREID".

### Select scope or claims

In the "SIOP" accordion, choose either
- `digiid_core` scope, or
- a set of claims (you can choose one claim or many).

Next choose "SEND LOGIN". An `openid` scheme URI representing a SIOP authentication request is created into the "Login response" field. In reality this URI would be created and presented by a SIOP-supporting relying party.

"Presentation definition" field is updated with the presentation definition that is automatically fetched and displayed, if it exists.

### Authenticate

Generate the vp_token and id_token by pressing "GENERATE AUTH".

Send the tokens to the backend by pressing "SEND AUTH".

View the credentials by pressing "CHECK THE STATUS" once the authentication response has been sent.

## SIOP-OIDC authentication flow

For this flow you should have two browser windows or tabs open.

Refresh the test wallet UI at http://localhost:3300 in the browser.

### Fetch test identity

Open "CoreId" accordion. Fetch a test identity from a local file with "FETCH COREID".

### Open relying party view (OIDC tester)

Navigate to http://localhost:**3030** in a second browser window/tab.

The view in the second window/tab simulates a relying party's login view. Choose "Login" and you are redirected to the OIDC adapter (op/op-ui).

Op-ui displays a QR code. Within the QR code, there is an `openid` scheme URI for a SIOP authentication request. This would in reality be read by the Suomi.fi Wallet mobile application.

Copy the QR code as a URI by pressing your right mouse button over the QR code and choosing "Copy link address" (or similar). Paste the copied URI as text into the "Login response" field in the "SIOP" section of the test wallet UI that is open in the other window/tab. Continue the authentication flow from there.

**Do not choose scopes or claims or press "SEND LOGIN" in the test wallet UI!** You do not need to generate a SIOP authentication request since it was already created by the op/op-ui.

### Authenticate

Generate the vp_token and id_token by pressing "GENERATE AUTH".

Send the tokens to the backend by pressing "SEND AUTH".

### Authentication redirect

You are now authenticated in the OIDC tester view and user identity attributes are automatically displayed in /users endpoint.

## VDR

Local Verifiable Data Registry service can be queried by choosing "VDR" accordion in the test wallet UI. See "Usage instructions" accordion for valid input parameters.
