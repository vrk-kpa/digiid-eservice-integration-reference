import { Box, Container } from "@mui/material";
import Image from "next/image";
import React from "react";
import { useIntl } from "react-intl";
import styled from "styled-components";
import { defaultSuomifiTheme, Heading } from "suomifi-ui-components";

import SelectorRadio from "./SelectorRadio";

const StyledContainer = styled(Container)`
  padding: 0;

  &:focus-within {
    border: none;
    border-radius: 2pt;
    outline: none;
    transition: 0.1s;
    box-shadow: 0 0 0 2pt ${defaultSuomifiTheme.colors.highlightBase};
  }
`;

const MethodSelector: React.FC<{
  method: number;
  onSelection: (method: number) => void;
}> = ({ onSelection, method }) => {
  const intl = useIntl();

  return (
    <StyledContainer>
      <Heading
        variant="h3"
        style={{ marginBottom: defaultSuomifiTheme.spacing.m }}
      >
        {intl.formatMessage({ id: "selector.header" })}
      </Heading>
      <Box sx={{ marginBottom: 0 }} display="flex">
        <SelectorRadio
          label={intl.formatMessage({ id: "selector.method1" })}
          checked={method === 1}
          sx={{ marginRight: 2 }}
          onSelection={() => onSelection(1)}
          value={1}
        >
          <Image
            src="/op-ui/method1.svg"
            alt="mobilePicture"
            width={60}
            height={60}
          />
        </SelectorRadio>
        <SelectorRadio
          label={intl.formatMessage({ id: "selector.method0" })}
          checked={method === 0}
          sx={{ marginRight: 2 }}
          onSelection={() => onSelection(0)}
          value={0}
        >
          <Image
            src="/op-ui/method0.svg"
            alt="mobilePicture"
            width={60}
            height={60}
          />
        </SelectorRadio>
      </Box>
    </StyledContainer>
  );
};

export default MethodSelector;
