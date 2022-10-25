import { getUserLocale } from "get-user-locale";
import { set, get } from "local-storage";
import { useRouter } from "next/router";
import React, { FC } from "react";
import { IntlProvider } from "react-intl";

import messagesEN from "../translations/en.json";
import messagesFI from "../translations/fi.json";
import messagesSV from "../translations/sv.json";

const loadLocaleData = (locale: string) => {
  switch (locale) {
    case "en":
      return messagesEN;

    case "sv":
      return messagesSV;

    case "fi":
    default:
      return messagesEN;
  }
};

export const getDefaultLocale = () => {
  let locale = get<string>("locale");

  if (!locale) {
    locale = getUserLocale();
  }

  switch (locale) {
    case "fi":
      return "fi";

    case "sv":
      return "sv";

    case "en":
    case "en-US":
    case "en-GB":
      return "en";

    default:
      return "en";
  }
};

export const LocaleProvider: FC = ({ children }) => {
  const router = useRouter();

  const [locale, setLocale] = React.useState<string>("en");

  React.useEffect(() => {
    const ui_locales = router.query.ui_locales
      ? (router.query.ui_locales as string).split(",")[0]
      : null;
    if (ui_locales != null) {
      set<string>("locale", ui_locales);
      setLocale(ui_locales);
    }

    setLocale(getDefaultLocale());
  }, [router.query.ui_locales]);

  return (
    <IntlProvider
      locale={locale}
      messages={loadLocaleData(locale)}
      defaultLocale={"en"}
      wrapRichTextChunksInFragment
    >
      {children}
    </IntlProvider>
  );
};
