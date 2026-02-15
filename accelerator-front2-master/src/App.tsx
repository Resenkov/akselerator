/* eslint-disable react-hooks/exhaustive-deps */
import { useLayoutEffect } from 'react'
import { Routes, Route } from 'react-router-dom'
import { CssBaseline } from '@mui/material'
import { ThemeProvider } from './contexts/ThemeContext'
import MainLayout from './layouts/MainLayout'
import HomePage from './pages/HomePage/HomePage'
import CatchPage from './pages/CatchPage/CatchPage'
import OverviewPage from './pages/OverviewPage/OverviewPage'
import MyCatchesPage from './pages/MyCatchesPage/MyCatchesPage'
import QuotasPage from './pages/QuotasPage/QuotasPage'
import LoginPage from './pages/LoginPage/LoginPage'
import RegisterPage from './pages/RegisterPage/RegisterPage'
import ContactPage from './pages/ContactPage/ContactPage'
import AdminMessagesPage from './pages/AdminMessagesPage/AdminMessagesPage'
import AccessDenied from './shared/components/AccessDenied'
import { ProtectedRoute } from './components/ProtectedRoute'
import { useAuthCheck } from './hooks/useAuthCheck'
import WinterAnimation from './components/Winter/Winter'
import NotFoundPage from './components/404'
import { useAppSelector } from './hooks/storeHooks'
import { useNavigate } from 'react-router-dom'
import Garland from './components/Winter/Garland'
import RegisterCompanyUser from './pages/RegisterCompanyUser/RegisterCompanyUser'

function App() {
	const navigate = useNavigate()
	const { roles } = useAppSelector(state => state.userProfile)

	// Проверка авторизации
	useAuthCheck()

	useLayoutEffect(() => {
		// Перенаправление на основе ролей
		if (roles?.includes('ADMIN')) {
			navigate('/quotas')
		} else if (roles?.includes('FISHERMAN')) {
			navigate('/catch')
		}
	}, [roles])

	return (
		<ThemeProvider>
			<CssBaseline />
			<WinterAnimation />
			<Garland />
			<MainLayout>
				<Routes>
					{/* Публичные маршруты (доступны всем) */}
					<Route path='/' element={<HomePage />} />
					<Route path='/login' element={<LoginPage />} />
					<Route path='/register' element={<RegisterPage />} />
					<Route path='/contact' element={<ContactPage />} />
					<Route path='/access-denied' element={<AccessDenied />} />

					<Route path='*' element={<NotFoundPage />} />

					{/* Маршруты для рыбаков */}
					<Route
						path='/catch'
						element={
							<ProtectedRoute requiredRoles={['FISHERMAN']} requireAuth={true}>
								<CatchPage />
							</ProtectedRoute>
						}
					/>

					<Route
						path='/my-catches'
						element={
							<ProtectedRoute requiredRoles={['FISHERMAN']} requireAuth={true}>
								<MyCatchesPage />
							</ProtectedRoute>
						}
					/>

					<Route
						path='/register_company_user'
						element={
							<ProtectedRoute requiredRoles={['FISHERMAN']} requireAuth={true}>
								<RegisterCompanyUser />
							</ProtectedRoute>
						}
					/>

					{/* Маршруты для рыбаков и админов */}
					<Route
						path='/overview'
						element={
							<ProtectedRoute requiredRoles={['ADMIN']} requireAuth={true}>
								<OverviewPage />
							</ProtectedRoute>
						}
					/>

					<Route
						path='/quotas'
						element={
							<ProtectedRoute requiredRoles={['ADMIN']} requireAuth={true}>
								<QuotasPage />
							</ProtectedRoute>
						}
					/>

					<Route
						path='/adminMessages'
						element={
							<ProtectedRoute requiredRoles={['ADMIN']} requireAuth={true}>
								<AdminMessagesPage />
							</ProtectedRoute>
						}
					/>
				</Routes>
			</MainLayout>
		</ThemeProvider>
	)
}

export default App
