import { Box, BoxProps, SxProps, Theme } from "@mui/material";
import React from "react";
import styled from "styled-components";
import { Text, defaultSuomifiTheme } from "suomifi-ui-components";

interface StyledBoxProps extends BoxProps {
  checked: boolean;
}

const StyledBox = styled((props: StyledBoxProps) => <Box {...props} />)`
  height: 125px;
  width: 125px;
  background-color: ${defaultSuomifiTheme.colors.whiteBase};
  padding: ${defaultSuomifiTheme.spacing.l};
  border-color: ${(props) =>
    props.checked
      ? defaultSuomifiTheme.colors.highlightBase
      : defaultSuomifiTheme.colors.depthLight1};
  border-width: ${(props) => (props.checked ? "4px" : "1px")};
  border-style: solid;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;

  & input {
    position: fixed;
    opacity: 0;
    pointer-events: none;
  }
`;

const SelectorRadio: React.FC<{
  sx?: SxProps<Theme>;
  value: number;
  label: string;
  onSelection: () => void;
  checked: boolean;
}> = ({ checked, onSelection, sx, value, label, children }) => (
  <>
    <Box display="flex" flexDirection="column" onClick={onSelection}>
      <StyledBox sx={{ ...sx }} checked={checked}>
        <input
          type="radio"
          value={value}
          name="method"
          onChange={onSelection}
          tabIndex={0}
        />
        {children}
      </StyledBox>
      <Text
        smallScreen
        variant="bold"
        style={{
          paddingRight: defaultSuomifiTheme.spacing.l,
          paddingLeft: defaultSuomifiTheme.spacing.s,
          marginBottom: defaultSuomifiTheme.spacing.xl,
          marginTop: defaultSuomifiTheme.spacing.xs,
          textAlign: "center",
        }}
      >
        {label}
      </Text>
    </Box>
  </>
);
export default SelectorRadio;
