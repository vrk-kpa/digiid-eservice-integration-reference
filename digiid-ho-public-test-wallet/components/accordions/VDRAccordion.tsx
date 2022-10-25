import React, { FC } from 'react';
import {
  Box, Button, MenuItem, Select, TextField,
} from '@mui/material';
import defaultData from '../../pages/defaultData.json';
import AccordionBase from './AccordionBase';
import UsageInstructionsAccordion from "./UsageInstructionsAccordion";

const VDRAccordion: FC = () => {
  const [backendVdr, setBackendVdr] = React.useState(defaultData.backendVdr);
  const [vdrEntity, setVdrEntity] = React.useState('');
  const [vdrResponse, setVdrResponse] = React.useState('');

  const getVdrInfo = async () => {
    console.log(`${backendVdr}/${vdrEntity}/did.json`)
    if (vdrEntity.length > 5) {
      try {
        const resp = await fetch(`${backendVdr}/${encodeURIComponent(vdrEntity)}/did.json`, {
          method: 'GET',
          mode: 'cors',
          headers: {
            'Content-Type': 'application/json;charset=utf-8',
          },
        });
        setVdrResponse(await resp.json());
      } catch (error) {
        if (error.response) {
          setVdrResponse(
            `ERROR:${error.response.status} : ${error.response.data}`,
          );
        } else if (error.request) {
          setVdrResponse(error.request);
        } else {
          setVdrResponse(`ERROR:${error.message}`);
        }
      }
    }
  };

  return (
    <AccordionBase title="VDR">
      <UsageInstructionsAccordion>
        <Box display="flex" flexDirection="column" m={1}>
          <p>Used to query the VDR registry. VDR currently contains the following DID documents:</p>
          <b>/vdr/hyt</b>
          <ul>
            <li>hyt060168-9861#key-6aff5949-7eef-4fac-9eb8-772e391a2021</li>
          </ul>
          <b>/vdr/issuer</b>
          <ul>
            <li>56e8b13b-f8ab-4e7d-9250-a106f24e6cdf#key-92d3651a-0868-4886-b70d-4a35b0493fb1</li>
            <li>7705abd9-3d96-428a-949d-e85b43f75f2f#key-0f1d9721-3921-4841-91ab-5d2c755b24f4</li>
          </ul>
        </Box>
      </UsageInstructionsAccordion>
      <Box display="flex" flexDirection="row" m={1}>
        <Box display="flex" flexDirection="column" m={1}>
          <Select
            sx={{ m: 1 }}
            value={backendVdr}
            label="backendVdr"
            onChange={(e) => setBackendVdr(e.target.value)}
          >
            <MenuItem value="http://localhost:8380/vdr/hyt">
              http://localhost:8380/vdr/hyt
            </MenuItem>
            <MenuItem value="http://localhost:8380/vdr/issuer">
              http://localhost:8380/vdr/issuer
            </MenuItem>
          </Select>

          <TextField
            InputLabelProps={{ shrink: true }}
            sx={{ m: 1 }}
            label="Entity (hyt/issuer)"
            onChange={(e) => setVdrEntity(e.target.value)}
            value={vdrEntity}
          />
          <Button variant="contained" onClick={getVdrInfo}>
            Send query
          </Button>
        </Box>
        <TextField
          InputLabelProps={{ shrink: true }}
          sx={{ m: 1, flexGrow: 1 }}
          label="Vdr response"
          multiline
          disabled
          rows={10}
          value={JSON.stringify(vdrResponse, null, 2)}
        />
      </Box>
    </AccordionBase>
  );
};

export default VDRAccordion;
