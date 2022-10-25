import { Box } from "@mui/material";
import type { NextPage } from "next";
import { useRouter } from "next/router";
import { useIntl } from "react-intl";
import {
  Button,
  Text,
  Heading,
  defaultSuomifiTheme,
} from "suomifi-ui-components";

import Layout from "../components/Layout";

const ErrorPage: NextPage = () => {
  const router = useRouter();
  const intl = useIntl();

  const ftn_spname = router.query.ftn_spname ? router.query.ftn_spname : "";

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
          {intl.formatMessage({ id: "error.header" })}
        </Heading>
        <Box
          display="flex"
          sx={{ marginBottom: defaultSuomifiTheme.spacing.xl }}
        >
          <Text style={{ marginBottom: defaultSuomifiTheme.spacing.l }}>
            {intl.formatMessage({ id: "error.expired" })}
          </Text>
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
