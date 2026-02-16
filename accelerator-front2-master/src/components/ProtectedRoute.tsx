// components/ProtectedRoute.tsx
import { Navigate, useLocation } from 'react-router-dom'
import { useAppSelector } from '../hooks/storeHooks'

interface ProtectedRouteProps {
	children: React.ReactNode
	requiredRoles?: string[]
	requireAuth?: boolean
}

export const ProtectedRoute = ({
	children,
	requiredRoles = [],
	requireAuth = true,
}: ProtectedRouteProps) => {
	const location = useLocation()
	const { token, roles } = useAppSelector(state => state.userProfile)

	// Если требуется авторизация, но токена нет
	if (requireAuth && !token) {
		return <Navigate to='/login' state={{ from: location }} replace />
	}

	// Если требуются определенные роли, проверяем их
	if (requiredRoles.length > 0) {
		const hasRequiredRole = requiredRoles.some(role => roles?.includes(role))
		if (!hasRequiredRole) {
			return <Navigate to='/access-denied' replace />
		}
	}

	return <>{children}</>
}
