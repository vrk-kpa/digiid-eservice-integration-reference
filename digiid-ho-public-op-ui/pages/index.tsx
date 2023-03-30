/* eslint-disable complexity */
/* eslint-disable max-statements */
import { Box } from "@mui/material";
import axios from "axios";
import type { NextPage } from "next";
import { useRouter } from "next/router";
import { useUserAgent } from "next-useragent";
import React from "react";
import { useIntl } from "react-intl";
import { Button, Text, defaultSuomifiTheme } from "suomifi-ui-components";

import Layout from "../components/Layout";
import MethodSelector from "../components/MethodSelector";
import MobileMethod from "../components/MobileMethod";
import QrCodeMethod from "../components/QrCodeMethod";
import useInterval from "../utils/useInterval";

const MainPage: NextPage<{
  uaString?: string;
}> = (props) => {
  const ua = useUserAgent(
    props.uaString ? props.uaString : window.navigator.userAgent,
  );

  const router = useRouter();
  const intl = useIntl();

  const [showAllMethods, setShowAllMethods] = React.useState(false);
  const [method, setMethod] = React.useState(ua.isMobile ? 0 : 1);

  const pollInterval = 5000;

  const queryData = router.query.code
    ? Buffer.from(router.query.code as string, "base64").toString()
    : "";

  const ftn_spname = router.query.ftn_spname ? router.query.ftn_spname : "";

  const fetchStatus = async () => {
    const url = new URL(queryData);

    const response = await axios.get(
      // eslint-disable-next-line no-process-env
      process.env.NEXT_PUBLIC_OP_HOST +
        "/status/" +
        url.searchParams.get("nonce"),
    );

    return response.data;
  };

  const sendCancel = async () => {
    const url = new URL(queryData);

    const response = await axios.get(
      // eslint-disable-next-line no-process-env
      process.env.NEXT_PUBLIC_OP_HOST +
        "/cancel/" +
        url.searchParams.get("nonce") +
        "?error=user_cancelled",
    );

    const data = await response.data;
    if (data.redirect) {
      window.location.href = data.redirect;
    }
  };

  useInterval(async () => {
    try {
      const status = await fetchStatus();
      if (status.status === "READY" || status.status === "FAILED") {
        if (status.redirect) {
          router.replace(
            "/end?redirect=" + status.redirect + "&ftn_spname=" + ftn_spname,
          );
          window.location.href = status.redirect;
        }
      }
    } catch (_err) {
      router.replace("/error?ftn_spname=" + ftn_spname);
    }
  }, pollInterval);

  return (
    <Layout title={intl.formatMessage({ id: "common.title" })}>
      <Box flex={1} display="flex" flexDirection="column">
        <Text style={{ marginBottom: defaultSuomifiTheme.spacing.xl }}>
          {intl.formatMessage({ id: "common.mainHeader" })} {ftn_spname}
        </Text>
        {showAllMethods && (
          <MethodSelector
            method={method}
            onSelection={(method) => setMethod(method)}
          />
        )}
        {method === 0 && (
          <MobileMethod
            showAllButton={!showAllMethods}
            href={queryData}
            onShowAll={() => {
              setShowAllMethods(true);
              setMethod(1);
            }}
          />
        )}
        {method === 1 && <QrCodeMethod href={queryData} />}
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
        onClick={async () => await sendCancel()}
      >
        {intl.formatMessage({ id: "common.cancel" })}
      </Button>
    </Layout>
  );
};

export default MainPage;

export function getServerSideProps(context: {
  req: {
    headers: {
      "user-agent": string;
    };
  };
}) {
  return {
    props: {
      uaString: context.req.headers["user-agent"],
    },
  };
}
