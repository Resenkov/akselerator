// hooks/useAuthCheck.ts
import { useEffect } from 'react'
import { useAppDispatch } from './storeHooks'
import { setData } from '../store/slices/userProfileSlice '
import { api } from '../api/api'
import type { IUserData } from '../interfaces'

export const useAuthCheck = () => {
	const dispatch = useAppDispatch()

	useEffect(() => {
		const checkAuth = async () => {
			const token = localStorage.getItem('token')

			if (!token) {
				return
			}

			try {
				const userData: IUserData = await api
					.get('/auth/token-info', {
						headers: {
							Authorization: `Bearer ${token}`,
						},
					})
					.then(res => res.data)

				dispatch(setData(userData))

				return userData
			} catch {
				console.error('Token verification failed, user not authenticated:')
				return null
			}
		}

		checkAuth()
	}, [dispatch])
}
