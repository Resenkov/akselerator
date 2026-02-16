import ReactDOM from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { store } from './store/store.ts'
import { Provider } from 'react-redux'
import { SnackbarProvider } from 'notistack'
import { BrowserRouter } from 'react-router-dom'

ReactDOM.createRoot(document.getElementById('root')!).render(
	<BrowserRouter>
		<Provider store={store}>
			<SnackbarProvider>
				<App />
			</SnackbarProvider>
		</Provider>
	</BrowserRouter>
)
