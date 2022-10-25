import '../styles/globals.css'
import { DigiWalletProvider } from '../components/DigiWalletProvider'

function MyApp({ Component, pageProps }) {
  return (<DigiWalletProvider>
    <Component {...pageProps} /></DigiWalletProvider>)
}

export default MyApp
