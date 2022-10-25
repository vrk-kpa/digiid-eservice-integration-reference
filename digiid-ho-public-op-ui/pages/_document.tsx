/* eslint-disable no-param-reassign */
import { ServerStyleSheets as MaterialUiServerStyleSheets } from "@mui/styles";
import Document, { Head, Html, Main, NextScript } from "next/document";
import { ServerStyleSheet } from "styled-components";

export default class MyDocument extends Document {
  render() {
    return (
      <Html lang="en">
        <Head>
          <link rel="shortcut icon" href="/static/favicon.ico" />
          <link
            rel="stylesheet"
            href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,600&display=swap"
          />
          {(this.props as any).styles}
        </Head>
        <body>
          <Main />
          <NextScript />
        </body>
      </Html>
    );
  }
}

MyDocument.getInitialProps = async (ctx) => {
  const originalRenderPage = ctx.renderPage;

  const sheet = new ServerStyleSheet();
  const materialUiSheets = new MaterialUiServerStyleSheets();

  ctx.renderPage = () =>
    originalRenderPage({
      enhanceApp: (App: any) =>
        function EnhanceApp(props) {
          return sheet.collectStyles(
            materialUiSheets.collect(<App {...props} />),
          );
        },
    });

  const initialProps = await Document.getInitialProps(ctx);

  return {
    ...initialProps,
    styles: (
      <>
        {initialProps.styles}
        {materialUiSheets.getStyleElement()}
        {sheet.getStyleElement()}
      </>
    ),
  };
};
