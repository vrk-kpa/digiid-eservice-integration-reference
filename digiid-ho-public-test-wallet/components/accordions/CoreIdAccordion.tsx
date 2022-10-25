import React, { FC } from 'react';
import {
  Box, Button, IconButton, MenuItem, Select, TextField, Tooltip,
} from '@mui/material';
import { QuestionMark } from '@mui/icons-material';
import { useDigiWallet } from '../DigiWalletProvider';
import defaultData from '../../pages/defaultData.json';
import AccordionBase from './AccordionBase';
import UsageInstructionsAccordion from './UsageInstructionsAccordion';
import coreId from "../../pages/coreID.json"


type Props = {
  setCoreId: (coreId: object) => void,
};

const CoreIdAccordion: FC<Props> = ({ setCoreId }: Props) => {
  const [coreIdInfo, setCoreIdInfo] = React.useState<string>();
  const [portrait, setPortrait] = React.useState<string>('');

  const getCoreId = async () => {
      try {
        setCoreId(coreId);
        setCoreIdInfo(JSON.stringify(coreId, null, 2));

        const portraitCredential = coreId.verifiableCredential?.find(
          (vc) => vc['type'][2] === 'portrait',
        );

        if (portraitCredential) {
          setPortrait(portraitCredential['credentialSubject']['portrait']);
        } else {
          setPortrait('');
        }
      } catch (error) {
        console.error(error);
        if (error.response) {
          setCoreIdInfo(
            `ERROR:${error.response.status} : ${error.response.data}`,
          );
        } else if (error.request) {
          setCoreIdInfo(error.request);
        } else {
          setCoreIdInfo(`ERROR:${error.message}`);
        }
      }
  };

  return (
    <AccordionBase title="CoreId">
      <UsageInstructionsAccordion>
        <Box display="flex" flexDirection="column" m={1}>
          Used to fetch personal details of the registered person from a local file.
          Data is returned in Verifiable Presentation -format.
        </Box>
      </UsageInstructionsAccordion>
      <Box display="flex" flexDirection="row" m={1}>
        <Box display="flex" flexDirection="column" m={1}>
          <Box display="flex" flexDirection="row" style={{ marginTop: 20 }}>
            <Button style={{ width: '100%' }} variant="contained" onClick={() => getCoreId()}>
              Fetch CoreId
            </Button>
            <Tooltip
              title="Fetch coreID from local file"
            >
              <IconButton>
                <QuestionMark />
              </IconButton>
            </Tooltip>
          </Box>
        </Box>
        <TextField
          InputLabelProps={{ shrink: true }}
          sx={{ m: 1, flexGrow: 1 }}
          id="coreId"
          label="CoreId"
          multiline
          disabled
          rows={10}
          defaultValue={coreIdInfo}
        />
        {portrait && (
        <Box
          component="img"
          sx={{
            m: 1, flexGrow: 1, maxHeight: 225, maxWidth: 225,
          }}
          src={`data:image/jpeg;base64,${portrait}`}
        />
        )}
      </Box>
    </AccordionBase>
  );
};

export default CoreIdAccordion;
