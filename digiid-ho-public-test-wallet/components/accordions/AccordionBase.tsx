import React, { FC } from 'react';
import { Accordion, AccordionDetails, AccordionSummary } from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

type Props = {
  title: string,
  children: React.ReactNode
};

const AccordionBase: FC<Props> = ({ title, children }: Props) => (
  <Accordion sx={{ marginBottom: 4 }}>
    <AccordionSummary
      sx={{ backgroundColor: 'rgba(4, 4, 4, .05)' }}
      expandIcon={<ExpandMoreIcon />}
      aria-controls="panel1a-content"
      id="panel1a-header"
    >
      {title}
    </AccordionSummary>
    <AccordionDetails>
      {children}
    </AccordionDetails>
  </Accordion>
);

export default AccordionBase;
