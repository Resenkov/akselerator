import React from 'react'
import {
	AppBar,
	Toolbar,
	Typography,
	Button,
	IconButton,
	Box,
	Container,
	Stack,
} from '@mui/material'
import { Brightness4, Brightness7 } from '@mui/icons-material'
import { useTheme } from '../contexts/ThemeContext'
import { useNavigate, useLocation } from 'react-router-dom'
import PhishingIcon from '@mui/icons-material/Phishing'
import { useAppSelector } from '../hooks/storeHooks'

interface Props {
	children: React.ReactNode
}

const MainLayout: React.FC<Props> = ({ children }) => {
	const { token, roles } = useAppSelector(state => state.userProfile)

	const { mode, toggleTheme } = useTheme()
	const navigate = useNavigate()
	const location = useLocation()

	const navigationItems = [
		...(roles.includes('FISHERMAN')
			? [
					{ label: 'Ввод улова', path: '/catch' },
					{ label: 'Мои уловы', path: '/my-catches' },
					{
						label: 'Регистрация пользователей',
						path: '/register_company_user',
					},
					{
						label: 'Связь',
						path: '/contact',
					},
			  ]
			: []),
		...(roles.includes('ADMIN')
			? [
					{ label: 'Обзор уловов', path: '/overview' },
					{ label: 'Квоты', path: '/quotas' },
					{ label: 'Сообщения', path: '/adminMessages' },
			  ]
			: []),
	]

	// Кнопки для неавторизованных пользователей
	const authButtons = !token ? (
		<Box sx={{ display: { xs: 'none', sm: 'flex' }, gap: 1, ml: 2 }}>
			<Button
				color='inherit'
				onClick={() => navigate('/login')}
				variant={location.pathname === '/login' ? 'outlined' : 'text'}
				size='small'
				sx={{ borderColor: 'rgba(255,255,255,0.3)' }}
			>
				Вход
			</Button>
			<Button
				color='inherit'
				onClick={() => navigate('/register')}
				variant={location.pathname === '/register' ? 'outlined' : 'text'}
				size='small'
				sx={{ borderColor: 'rgba(255,255,255,0.3)' }}
			>
				Регистрация
			</Button>
		</Box>
	) : null

	return (
		<Box
			sx={{
				display: 'flex',
				flexDirection: 'column',
				minHeight: '100vh',
				background:
					mode === 'light'
						? 'linear-gradient(180deg, #f8f9fa 0%, #f1f3f4 50%, #e8eaed 100%)'
						: '',
			}}
		>
			{/* Шапка */}
			<AppBar position='static' sx={{ flexShrink: 0 }}>
				<Container maxWidth='lg' disableGutters>
					<Toolbar
						sx={{
							justifyContent: 'space-between',
							minHeight: '64px !important',
							px: { xs: 2, sm: 3 },
						}}
					>
						{/* Логотип и название */}
						{!token && (
							<Stack direction='row' alignItems='center' gap={1}>
								<PhishingIcon />
								<Typography
									variant='h6'
									component='div'
									sx={{
										cursor: 'pointer',
										fontWeight: 'bold',
										display: 'flex',
										alignItems: 'center',
										gap: 1,
										flexShrink: 0,
									}}
									onClick={() => navigate('/')}
								>
									Рыболовный учёт
								</Typography>
							</Stack>
						)}

						{/* Навигация */}
						<Box
							sx={{
								display: { xs: 'none', md: 'flex' },
								gap: 1,
								flex: 1,
								justifyContent: 'center',
								mx: 2,
							}}
						>
							{navigationItems.map(item => (
								<Button
									key={item.path}
									color='inherit'
									onClick={() => navigate(item.path)}
									variant={
										location.pathname === item.path ? 'outlined' : 'text'
									}
									size='small'
									sx={{
										borderColor: 'rgba(255,255,255,0.3)',
									}}
								>
									{item.label}
								</Button>
							))}
						</Box>

						{/* Правая часть: кнопки авторизации или переключение темы */}
						<Box
							sx={{
								display: 'flex',
								alignItems: 'center',
								gap: 1,
								flexShrink: 0,
							}}
						>
							{authButtons}
							<IconButton
								color='inherit'
								onClick={toggleTheme}
								sx={{
									'&:hover': {
										backgroundColor: 'rgba(255, 255, 255, 0.1)',
									},
								}}
							>
								{mode === 'dark' ? <Brightness7 /> : <Brightness4 />}
							</IconButton>
						</Box>
					</Toolbar>
				</Container>
			</AppBar>

			{/* Основной контент */}

			<Container
				component='main'
				maxWidth='lg'
				sx={{
					flex: 1,
					py: 3,
					px: { xs: 2, sm: 3 },
				}}
			>
				{children}
			</Container>
		</Box>
	)
}

export default MainLayout
