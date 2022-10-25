import { Box } from "@mui/material";
import type { NextPage } from "next";
import NextLink from "next/link";
import { useRouter } from "next/router";
import { useIntl } from "react-intl";
import {
  Button,
  Text,
  Heading,
  Link,
  defaultSuomifiTheme,
} from "suomifi-ui-components";

import Layout from "../components/Layout";

const ErrorPage: NextPage = () => {
  const router = useRouter();
  const intl = useIntl();

  const ftn_spname = router.query.ftn_spname ? router.query.ftn_spname : "";
  const redirect = router.query.redirect ? router.query.redirect : "";

  return (
    <Layout title={intl.formatMessage({ id: "common.title" })}>
      <Box flex={1} display="flex" flexDirection="column">
        <Text style={{ marginBottom: defaultSuomifiTheme.spacing.xl }}>
          {intl.formatMessage({ id: "common.mainHeader" })} {ftn_spname}
        </Text>
        <Heading
          variant="h3"
          style={{ marginBottom: defaultSuomifiTheme.spacing.xl }}
        >
          {intl.formatMessage({ id: "success.header" })}
        </Heading>
        <Box
          display="flex"
          sx={{ marginBottom: defaultSuomifiTheme.spacing.xl }}
        >
          <Box sx={{ marginLeft: defaultSuomifiTheme.spacing.l }}>
            <Text style={{ marginBottom: defaultSuomifiTheme.spacing.xl }}>
              {intl.formatMessage({ id: "success.body1" })}
              <NextLink href={redirect as string} passHref>
                <Link href={redirect as string}>
                  <a>{intl.formatMessage({ id: "success.link" })}</a>
                </Link>
              </NextLink>
            </Text>
          </Box>
        </Box>
        <hr
          style={{
            borderTop: "1px solid " + defaultSuomifiTheme.colors.depthLight1,
            marginBottom: 20,
            width: "100%",
          }}
        />
      </Box>
      <Button
        variant="secondary"
        icon="arrowLeft"
        onClick={() => router.back()}
      >
        {intl.formatMessage({ id: "common.cancel" })}
      </Button>
    </Layout>
  );
};

export default ErrorPage;
