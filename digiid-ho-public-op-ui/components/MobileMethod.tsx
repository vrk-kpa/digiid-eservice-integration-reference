import { Grid, Box } from "@mui/material";
import Image from "next/image";
import React from "react";
import { useIntl } from "react-intl";
import { Text, defaultSuomifiTheme, Button } from "suomifi-ui-components";

const MobileMethod: React.FC<{
  href: string;
  showAllButton: boolean;
  onShowAll: () => void;
}> = ({ href, onShowAll, showAllButton }) => {
  const intl = useIntl();

  return (
    <>
      <Grid container sx={{ marginBottom: defaultSuomifiTheme.spacing.xl }}>
        <Grid
          item
          xs={12}
          md={6}
          display={showAllButton ? "flex" : "none"}
          justifyContent="center"
          sx={{ marginBottom: 4 }}
        >
          <Image
            src="/op-ui/mobile.svg"
            alt="mobilePicture"
            width={70}
            height={96}
          />
        </Grid>
        <Grid item xs={12} md={6} sx={{ marginLeft: { xs: 0, md: 0 } }}>
          <Box display="flex" flexDirection={"column"}>
            <Text style={{ marginBottom: defaultSuomifiTheme.spacing.xl }}>
              {intl.formatMessage({ id: "mobilemethod.body" })}
            </Text>
            <Button
              variant="default"
              onClick={() => window.open(href, "_blank")}
              style={{ marginBottom: 20 }}
            >
              {intl.formatMessage({ id: "mobilemethod.open" })}
            </Button>
            <Button
              variant="secondaryNoBorder"
              onClick={() => onShowAll()}
              style={{
                marginBottom: 30,
                display: showAllButton ? "block" : "none",
              }}
            >
              {intl.formatMessage({ id: "mobilemethod.showAll" })}
            </Button>
          </Box>
        </Grid>
      </Grid>
    </>
  );
};

export default MobileMethod;
