import NextApp from "next/app";
import { createGlobalStyle, ThemeProvider } from "styled-components";

// import type { AppProps } from 'next/app'
import { LocaleProvider } from "../utils/LocaleProvider";

const GlobalStyle = createGlobalStyle`
html {
  height: 100%;
}
body {
  height: 100%;
  padding: 0;
  margin: 0;
  background-color: #F7F7F8;
}

#__next {
  height: 100%;
}

* {
  box-sizing: border-box;
}
`;
const theme = {
  colors: {
    primary: "#0070f3",
  },
};

export default class App extends NextApp {
  // remove it here
  componentDidMount() {
    const jssStyles = document.querySelector("#jss-server-side");
    if (jssStyles && jssStyles.parentNode) {
      jssStyles.parentNode.removeChild(jssStyles);
    }
  }

  render() {
    const { Component, pageProps } = this.props;

    return (
      <>
        <GlobalStyle />
        <LocaleProvider>
          <ThemeProvider theme={theme}>
            <Component {...pageProps} />
          </ThemeProvider>
        </LocaleProvider>
      </>
    );
  }
}
