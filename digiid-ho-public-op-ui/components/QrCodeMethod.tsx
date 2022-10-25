import { Grid } from "@mui/material";
import React from "react";
import { useIntl } from "react-intl";
import QRCode from "react-qr-code";
import { Text, defaultSuomifiTheme, Heading } from "suomifi-ui-components";

const QrCodeMethod: React.FC<{ href: string }> = ({ href }) => {
  const intl = useIntl();

  return (
    <>
      <Heading
        variant="h3"
        style={{
          marginBottom: defaultSuomifiTheme.spacing.xxs,
        }}
        as="h2"
      >
        {intl.formatMessage({ id: "qrmethod.header" })}
      </Heading>

      <Grid container sx={{ marginBottom: defaultSuomifiTheme.spacing.xl }}>
        <Grid
          item
          xs={12}
          md={6}
          justifyContent="center"
          display="flex"
          sx={{
            padding: {
              xs: defaultSuomifiTheme.spacing.l,
              md: defaultSuomifiTheme.spacing.s,
            },
            marginTop: defaultSuomifiTheme.spacing.m,
            borderColor: defaultSuomifiTheme.colors.depthLight1,
            borderWidth: 1,
            borderStyle: "solid",
            borderRadius: 2,
          }}
        >
          <a
            href={href}
            aria-label={intl.formatMessage({ id: "qrmethod.qrcode" })}
          >
            <QRCode value={href} />
          </a>
        </Grid>
        <Grid item xs={12} md={6} sx={{ paddingLeft: { xs: 0, md: 3 } }}>
          <Text style={{ marginBottom: defaultSuomifiTheme.spacing.xl }}>
            <ol style={{ paddingLeft: 16 }}>
              <li style={{ paddingBottom: 20 }}>
                {intl.formatMessage({ id: "qrmethod.body1" })}
              </li>
              <li>{intl.formatMessage({ id: "qrmethod.body2" })}</li>
            </ol>
            {intl.formatMessage({ id: "qrmethod.body3" })}
          </Text>
        </Grid>
      </Grid>
    </>
  );
};

export default QrCodeMethod;
