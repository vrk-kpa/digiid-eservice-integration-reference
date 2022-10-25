import { Box } from "@mui/material";
import Head from "next/head";
import React, { ReactNode } from "react";
import { Heading, defaultSuomifiTheme } from "suomifi-ui-components";

type Props = {
  children?: ReactNode;
  title?: string;
};

const Layout: React.FC<Props> = ({ children, title }: Props) => (
  <>
    <Head>
      <title>{title}</title>
      <meta charSet="utf-8" />
      <meta name="robots" content="noindex" />
      <meta name="viewport" content="initial-scale=1.0, width=device-width" />
    </Head>
    <Box
      flex={1}
      display="flex"
      flexDirection={"column"}
      sx={{
        height: "100%",
        width: "100%",
        paddingTop: defaultSuomifiTheme.spacing.xxl,
      }}
    >
      <Box
        flex={1}
        display="flex"
        flexDirection="column"
        sx={{
          margin: "auto",
          width: {
            xs: "100%",
            md: 650,
          },
        }}
      >
        <Heading smallScreen variant="h1">
          <Box
            sx={{
              marginLeft: {
                xs: defaultSuomifiTheme.spacing.l,
                md: 0,
              },
            }}
          >
            {title}
          </Box>
        </Heading>
        <Box
          sx={{
            backgroundColor: defaultSuomifiTheme.colors.whiteBase,
            padding: {
              xs: defaultSuomifiTheme.spacing.l,
              md: defaultSuomifiTheme.spacing.xxl,
            },
            paddingBottom: defaultSuomifiTheme.spacing.m,
            marginTop: defaultSuomifiTheme.spacing.m,
            borderColor: defaultSuomifiTheme.colors.depthLight1,
            borderWidth: 1,
            borderStyle: "solid",
            borderRadius: 2,
          }}
        >
          {children}
        </Box>
      </Box>
      <Box
        flexGrow={1}
        sx={{
          backgroundColor: defaultSuomifiTheme.colors.whiteBase,
          marginTop: defaultSuomifiTheme.spacing.xxxxl,
        }}
      ></Box>
    </Box>
  </>
);

export default Layout;
