import { useState, useEffect } from 'react'

export const useLocalStorage = (key: string) => {
	const [value, setValue] = useState<string | null>(() => {
		return localStorage.getItem(key)
	})

	useEffect(() => {
		const handleStorageChange = () => {
			setValue(localStorage.getItem(key))
		}

		// Слушаем изменения из других вкладок
		window.addEventListener('storage', handleStorageChange)

		// Слушаем кастомные события из этой вкладки
		window.addEventListener('localStorageChange', handleStorageChange)

		return () => {
			window.removeEventListener('storage', handleStorageChange)
			window.removeEventListener('localStorageChange', handleStorageChange)
		}
	}, [key])

	return value
}
