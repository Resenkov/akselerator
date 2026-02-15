/* eslint-disable react-refresh/only-export-components */
import React, {
	createContext,
	useContext,
	useState,
	type ReactNode,
	useEffect,
	useMemo,
} from 'react'
import {
	ThemeProvider as MUIThemeProvider,
	createTheme,
} from '@mui/material/styles'

type ThemeMode = 'light' | 'dark'

interface ThemeContextType {
	mode: ThemeMode
	toggleTheme: () => void
}

const ThemeContext = createContext<ThemeContextType>({
	mode: 'light',
	toggleTheme: () => {},
})

export const useTheme = () => {
	return useContext(ThemeContext)
}

interface ThemeProviderProps {
	children: ReactNode
}

export const ThemeProvider: React.FC<ThemeProviderProps> = ({ children }) => {
	// Инициализируем состояние с учетом localStorage
	const [mode, setMode] = useState<ThemeMode>('light')

	// При монтировании компонента читаем тему из localStorage
	useEffect(() => {
		// Проверяем, что код выполняется на клиенте
		if (typeof window !== 'undefined') {
			const savedMode = localStorage.getItem('theme') as ThemeMode | null
			if (savedMode && (savedMode === 'light' || savedMode === 'dark')) {
				setMode(savedMode)
			}
		}
	}, [])

	// Сохраняем тему в localStorage при изменении
	useEffect(() => {
		if (typeof window !== 'undefined') {
			localStorage.setItem('theme', mode)
		}
	}, [mode])

	const toggleTheme = () => {
		setMode(prevMode => (prevMode === 'light' ? 'dark' : 'light'))
	}

	// Используем useMemo для оптимизации создания темы
	const theme = useMemo(
		() =>
			createTheme({
				palette: {
					mode,
					primary: {
						main: mode === 'light' ? '#1976d2' : '#90caf9',
					},
					secondary: {
						main: mode === 'light' ? '#dc004e' : '#f48fb1',
					},
				},
			}),
		[mode]
	)

	return (
		<ThemeContext.Provider value={{ mode, toggleTheme }}>
			<MUIThemeProvider theme={theme}>{children}</MUIThemeProvider>
		</ThemeContext.Provider>
	)
}
