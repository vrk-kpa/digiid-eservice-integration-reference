import React, { createContext, FC, useContext } from "react";
import { set, get } from "local-storage";

type DigiIdWalletContextType = {
  getPrivateKey: () => string;
  getPublicKey: () => string;
  getCert: () => { crt: string; deviceId: string; keyId: string };
};

const DigiIdWalletContext = createContext<DigiIdWalletContextType | undefined>(
  undefined
);

export const useDigiWallet = () => {
  const context = useContext(DigiIdWalletContext);

  if (context === undefined) {
    throw new Error("useDigiWallet must be used within a DigiWalletProvider");
  }

  return context;
};

export const DigiWalletProvider: FC = ({ children }) => {
  const getPrivateKey = () =>  "-----BEGIN PRIVATE KEY-----\nMIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgrz3a1ga+hS9L9Ytq+c+a84rNFD9VGCJZpEjc3wGYhUSgCgYIKoZIzj0DAQehRANCAARiUy+OgxiR4LPU8JDlqCBMmlTeLBLYMH7HPQgckkgGBhyKKeCrKFouqzJRtt3DJnkhxA1mIhDcphYASb8lEb3O\n-----END PRIVATE KEY-----\n"

  const getPublicKey = () => "-----BEGIN PUBLIC KEY-----\nMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEYlMvjoMYkeCz1PCQ5aggTJpU3iwS2DB+xz0IHJJIBgYciingqyhaLqsyUbbdwyZ5IcQNZiIQ3KYWAEm/JRG9zg==\n-----END PUBLIC KEY-----\n"

  const getCert = () => ({
       "crt":"-----BEGIN CERTIFICATE-----\\nMIIC6DCB0aADAgECAgUAqjEHujANBgkqhkiG9w0BAQsFADAeMRwwGgYDVQQDDBNU\\nZXN0IENlcnQgQXV0aG9yaXR5MB4XDTIyMTAyMDExMjAwM1oXDTIzMTAyMDExMjAw\\nM1owGTEXMBUGA1UEAwwOaHl0MDYwMTY4LTk4NjEwWTATBgcqhkjOPQIBBggqhkjO\\nPQMBBwNCAARiUy+OgxiR4LPU8JDlqCBMmlTeLBLYMH7HPQgckkgGBhyKKeCrKFou\\nqzJRtt3DJnkhxA1mIhDcphYASb8lEb3OMA0GCSqGSIb3DQEBCwUAA4ICAQA3yNqA\\nENzO1HmJllmNM1UKRfHMJ8EwUXO7sknZUqHVS53thkdKFj4KnhCRorGkinrFr20c\\no3JDX2vxXvi1azCj91ENI1YNeNNXt5NldAEA2eNbkCRIsl78rlN9U8MaL0YP0r4x\\nqt+XzPAqpxQ/RMmJYcS2f+2Mvqg/nY6H+wAizelUNltJFIkQ/ODZj0BSs0jZ4SAM\\nAHZD+p7Mbwm1QqL8u3pzynHb7eQRQEbZid+qwtVrRNA6kV7FFcHQxqzgXsP9o8tR\\nicC6TY8PFTXnYWcivBoOYzmBNcW2idpGy1OC9UU7WQ5bFFQ0FHA8/Obtl+KYDItS\\npLoFXBvJ90jVV5LQLfa5+oF0gxbybQERFSBIUHRYVeXndmaNzYJGAmiFzd9gk23l\\n0qKpfYpFQnuZNf0J+OcuLQ1mhSGb5zHHV1+ZlEbh41rE2ZVN95TA3x7673xE04XW\\nltvzEaMOzaZYl8ypumyUsKgOQLJy4qpd+o8/9IicUQt8iqIpGhBBXZCOMtwapY2w\\nYZa+FsUWpt2LWINnwDUb7oc2Y0YYa9YEMh4RfqPU4IZffB5QKYWGkOiqL/G0OXOi\\nue3UoEJkEJOFH5V8W15VWuIvrYNZ84w2KfC11qmGghhUIUCINEfXqxWSfsV2qrr6\\nKYftiH0wHWIPrz7V3jxOSzZzFzOM48dBcIAWoQ==\\n-----END CERTIFICATE-----\\n",
       "deviceId":"f1835bd7-da96-4d52-8b21-981a349e996a",
       "keyId":"did:web:localhost%3A8380:vdr:hyt:hyt060168-9861#key-6aff5949-7eef-4fac-9eb8-772e391a2021"
    });

  return (
    <DigiIdWalletContext.Provider
      value={{
        getPrivateKey,
        getPublicKey,
        getCert,
      }}
    >
      {children}
    </DigiIdWalletContext.Provider>
  );
};
