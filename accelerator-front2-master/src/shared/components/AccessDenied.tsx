// components/AccessDenied.tsx
import { Box, Typography, Button, Paper, Alert } from '@mui/material'
import { Login, Security, Home } from '@mui/icons-material'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAppSelector } from '../../hooks/storeHooks'

interface AccessDeniedProps {
	message?: string
	showLoginButton?: boolean
	showHomeButton?: boolean
	customAction?: () => void
	severity?: 'error' | 'warning' | 'info'
}

const AccessDenied: React.FC<AccessDeniedProps> = ({
	message = 'У вас нет прав для просмотра данной страницы',
	showLoginButton = true,
	showHomeButton = true,
	customAction,
	severity = 'error',
}) => {
	const token = useAppSelector(state => state.userProfile.token)

	const navigate = useNavigate()
	const location = useLocation()

	const handleLogin = () => {
		if (customAction) {
			customAction()
		} else {
			// Сохраняем текущий путь для редиректа после логина
			navigate('/login', { state: { from: location } })
		}
	}

	const handleGoHome = () => {
		navigate('/')
	}

	return (
		<Box
			display='flex'
			justifyContent='center'
			alignItems='center'
			minHeight='70vh'
			p={3}
		>
			<Paper
				elevation={3}
				sx={{
					p: 4,
					textAlign: 'center',
					maxWidth: 450,
					borderRadius: 2,
					width: '100%',
				}}
			>
				<Security
					sx={{
						fontSize: 64,
						color: severity === 'error' ? 'error.main' : 'warning.main',
						mb: 2,
					}}
				/>

				<Alert severity={severity} sx={{ mb: 3, justifyContent: 'center' }}>
					Доступ запрещен
				</Alert>

				<Typography
					variant='body1'
					color='text.secondary'
					sx={{ mb: 4, lineHeight: 1.6 }}
				>
					{message}
				</Typography>

				{token ? (
					<Button
						onClick={() => navigate(-1)}
						className='px-4 py-2 bg-blue-500 text-white rounded'
					>
						Назад
					</Button>
				) : (
					<Box display='flex' gap={2} justifyContent='center' flexWrap='wrap'>
						{showLoginButton && (
							<Button
								variant='contained'
								startIcon={<Login />}
								onClick={handleLogin}
								size='large'
								sx={{ borderRadius: 2 }}
							>
								Войти
							</Button>
						)}

						{showHomeButton && (
							<Button
								variant='outlined'
								startIcon={<Home />}
								onClick={handleGoHome}
								size='large'
								sx={{ borderRadius: 2 }}
							>
								На главную
							</Button>
						)}
					</Box>
				)}
			</Paper>
		</Box>
	)
}

export default AccessDenied
