import { Box } from '@mui/material';

import React from 'react';
import Layout from '../components/Layout';
import CoreIdAccordion from '../components/accordions/CoreIdAccordion';
import SIOPAccordion from '../components/accordions/SIOPAccordion';
import VDRAccordion from "../components/accordions/VDRAccordion";

const IndexPage: React.FC = () => {
  const [coreId, setCoreId] = React.useState<object>(
  );

  return (
      <Layout title="Digi id: test web wallet">
        <h1>Test web wallet</h1>
        <Box display="flex" flexDirection="column" p={3}>
          <CoreIdAccordion setCoreId={setCoreId} />
          <SIOPAccordion coreId={coreId} />
          <VDRAccordion />
        </Box>
      </Layout>
  );
};

export default IndexPage;
