/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/no-unused-vars */
import React, { useState } from 'react'
import {
	Container,
	Paper,
	Box,
	TextField,
	Button,
	Typography,
	Alert,
	CircularProgress,
	Divider,
	Fade,
} from '@mui/material'
import {
	Login,
	Visibility,
	VisibilityOff,
	Person,
	Lock,
} from '@mui/icons-material'
import { useNavigate } from 'react-router-dom'
import { Link } from 'react-router-dom'
import { api } from '../../api/api'
import { useSnackbar } from 'notistack'
import { type IUserData } from '../../interfaces'
import { setData } from '../../store/slices/userProfileSlice '
import { useDispatch } from 'react-redux'

const LoginPage: React.FC = () => {
	const navigate = useNavigate()
	const { enqueueSnackbar } = useSnackbar()
	const dispatch = useDispatch()

	const [formData, setFormData] = useState({
		username: '',
		password: '',
	})
	const [showPassword, setShowPassword] = useState(false)
	const [isLoading, setIsLoading] = useState(false)
	const [error, setError] = useState('')

	const handleInputChange =
		(field: string) => (event: React.ChangeEvent<HTMLInputElement>) => {
			setFormData(prev => ({ ...prev, [field]: event.target.value }))
			if (error) setError('')
		}

	const handleSubmit = async (event: React.FormEvent) => {
		event.preventDefault()

		// Валидация формы
		if (!formData.username.trim() || !formData.password.trim()) {
			setError('Заполните все обязательные поля')
			return
		}

		setIsLoading(true)
		setError('')

		try {
			// Имитация запроса к API
			const response: IUserData = await api
				.post('/auth/login', {
					username: formData.username,
					password: formData.password,
				})
				.then(res => res.data)

			localStorage.setItem('token', response.token || '')

			dispatch(setData(response))

			// Триггерим событие для обновления App.tsx
			window.dispatchEvent(new Event('localStorageChange'))

			enqueueSnackbar('Вход прошел успешно', { variant: 'success' })
			await new Promise(resolve => setTimeout(resolve, 1000))

			if (response.roles.includes('FISHERMAN')) navigate('/catch')
			else navigate('/quotas')
		} catch (err: any) {
			// Обработка ошибок от API
			const errorMessage =
				err.response?.data?.message ||
				err.response?.data?.error ||
				'Ошибка при входе в систему. Проверьте логин и пароль.'
			setError(errorMessage)

			// Показываем snackbar с ошибкой
			enqueueSnackbar(errorMessage, { variant: 'error' })
		} finally {
			setIsLoading(false)
		}
	}

	const isFormValid = formData.username.trim() && formData.password.trim()

	return (
		<Fade in={true} timeout={600}>
			<Container
				maxWidth='sm'
				sx={{
					minHeight: '100vh',
					display: 'flex',
					alignItems: 'center',
					justifyContent: 'center',
					py: 4,
				}}
			>
				<Paper
					elevation={8}
					sx={{
						width: '100%',
						p: 4,
						borderRadius: 2,
						// background: 'linear-gradient(145deg, #ffffff 0%, #f8f9fa 100%)',
					}}
				>
					{/* Заголовок */}
					<Box sx={{ textAlign: 'center', mb: 4 }}>
						<Typography
							variant='h3'
							component='h1'
							gutterBottom
							sx={{
								fontWeight: 'bold',
								background: 'linear-gradient(135deg, #1976d2 0%, #42a5f5 100%)',
								backgroundClip: 'text',
								WebkitBackgroundClip: 'text',
								color: 'transparent',
							}}
						>
							🎣 Рыболовный учёт
						</Typography>
						<Typography
							variant='h5'
							component='h2'
							gutterBottom
							sx={{ fontWeight: 'medium' }}
						>
							Вход в систему
						</Typography>
						<Typography variant='body2' color='text.secondary'>
							Введите ваши учетные данные для доступа
						</Typography>
					</Box>
					{/* Блок с ошибками */}
					{error && (
						<Alert
							severity='error'
							sx={{
								mb: 3,
								'& .MuiAlert-message': {
									width: '100%',
								},
							}}
							onClose={() => setError('')}
						>
							<Typography variant='body2' sx={{ fontWeight: 'medium' }}>
								{error}
							</Typography>
						</Alert>
					)}
					{/* Форма входа */}
					<Box component='form' onSubmit={handleSubmit}>
						<Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
							{/* Поле username */}
							<TextField
								label='Имя пользователя'
								value={formData.username}
								onChange={handleInputChange('username')}
								required
								fullWidth
								error={
									error.includes('логин') || error.includes('пользователь')
								}
								InputProps={{
									startAdornment: (
										<Person sx={{ color: 'text.secondary', mr: 1 }} />
									),
								}}
								placeholder='Введите имя пользователя'
							/>
							{/* Поле password */}
							<TextField
								label='Пароль'
								type={showPassword ? 'text' : 'password'}
								value={formData.password}
								onChange={handleInputChange('password')}
								required
								fullWidth
								error={error.includes('пароль')}
								InputProps={{
									startAdornment: (
										<Lock sx={{ color: 'text.secondary', mr: 1 }} />
									),
									endAdornment: (
										<Button
											size='small'
											onClick={() => setShowPassword(!showPassword)}
											sx={{ minWidth: 'auto', p: 0.5 }}
										>
											{showPassword ? <VisibilityOff /> : <Visibility />}
										</Button>
									),
								}}
								placeholder='Введите пароль'
							/>
						</Box>
						{/* Кнопка входа */}
						<Button
							type='submit'
							variant='contained'
							fullWidth
							size='large'
							disabled={!isFormValid || isLoading}
							startIcon={isLoading ? <CircularProgress size={20} /> : <Login />}
							sx={{
								mt: 3,
								py: 1.5,
								fontSize: '1.1rem',
								fontWeight: 'bold',
								// background: 'linear-gradient(135deg, #1976d2 0%, #42a5f5 100%)',
								'&:hover': {
									background:
										'linear-gradient(135deg, #1565c0 0%, #1e88e5 100%)',
								},
								// '&:disabled': {
								// 	background: '#e0e0e0',
								// },
							}}
						>
							{isLoading ? 'Вход...' : 'Войти'}
						</Button>
					</Box>
					<Divider sx={{ my: 4 }}>
						<Typography variant='body2' color='text.secondary'>
							Нет аккаунта?
						</Typography>
					</Divider>
					{/* Ссылка на регистрацию */}
					<Box sx={{ textAlign: 'center' }}>
						<Button
							component={Link}
							to='/register'
							variant='outlined'
							fullWidth
							sx={{
								py: 1.5,
								fontWeight: 'bold',
							}}
						>
							Создать новый аккаунт
						</Button>
					</Box>
				</Paper>
			</Container>
		</Fade>
	)
}

export default LoginPage
