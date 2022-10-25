import React, { FC } from 'react';
import { Accordion, AccordionDetails, AccordionSummary } from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

type Props = {
  children: React.ReactNode
};

const UsageInstructionsAccordion: FC<Props> = ({ children }: Props) => (
  <Accordion sx={{ marginBottom: 4 }}>
    <AccordionSummary
      sx={{ backgroundColor: 'rgba(4, 4, 4, .05)' }}
      expandIcon={<ExpandMoreIcon />}
      aria-controls="panel1a-content"
      id="panel1a-header"
    >
      Usage instructions
    </AccordionSummary>
    <AccordionDetails>
      {children}
    </AccordionDetails>
  </Accordion>
);

export default UsageInstructionsAccordion;
