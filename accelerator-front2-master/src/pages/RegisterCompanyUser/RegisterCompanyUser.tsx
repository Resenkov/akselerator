/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
import React, { useState } from 'react'
import {
	Container,
	Paper,
	Box,
	Button,
	Typography,
	Alert,
	TextField,
	Stack,
	InputAdornment,
	IconButton,
	CircularProgress,
	Fade,
} from '@mui/material'
import { useNavigate } from 'react-router-dom'
import { Visibility, VisibilityOff } from '@mui/icons-material'
import { api } from '../../api/api'
import { useSnackbar } from 'notistack'
import { type IUserData } from '../../interfaces'
import { useDispatch } from 'react-redux'
import { setData } from '../../store/slices/userProfileSlice '
import { useAppSelector } from '../../hooks/storeHooks'

interface FormData {
	username: string
	email: string
	password: string
}

const RegisterCompanyUser: React.FC = () => {
	const navigate = useNavigate()
	const { enqueueSnackbar } = useSnackbar()
	const dispatch = useDispatch()

	const [formData, setFormData] = useState<FormData>({
		username: '',
		email: '',
		password: '',
	})

	const [isLoading, setIsLoading] = useState(false)
	const [error, setError] = useState('')
	const [showPassword, setShowPassword] = useState(false)

	const handleInputChange = (
		e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
	) => {
		const { name, value } = e.target

		// Специальная обработка для ИНН - только цифры
		if (name === 'inn') {
			// Удаляем все не-цифры
			const numbersOnly = value.replace(/\D/g, '')
			setFormData(prev => ({
				...prev,
				[name]: numbersOnly,
			}))
		} else {
			setFormData(prev => ({
				...prev,
				[name]: value,
			}))
		}

		// Очищаем ошибку при изменении поля
		if (error) setError('')
	}

	const handleClickShowPassword = () => {
		setShowPassword(!showPassword)
	}

	const validateForm = (): boolean => {
		// Проверка на пустые поля
		const requiredFields: (keyof FormData)[] = ['username', 'email', 'password']

		for (const field of requiredFields) {
			if (!formData[field].trim()) {
				setError(`Поле "${getFieldLabel(field)}" обязательно для заполнения`)
				return false
			}
		}

		// Валидация email
		const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
		if (!emailRegex.test(formData.email)) {
			setError('Введите корректный email адрес')
			return false
		}

		// Валидация пароля (минимум 6 символов)
		if (formData.password.length < 6) {
			setError('Пароль должен содержать минимум 6 символов')
			return false
		}

		// Валидация логина (только латинские буквы, цифры и подчеркивание)
		const usernameRegex = /^[a-zA-Z0-9_]+$/
		if (!usernameRegex.test(formData.username)) {
			setError(
				'Логин может содержать только латинские буквы, цифры и символ подчеркивания'
			)
			return false
		}

		return true
	}

	const getFieldLabel = (field: keyof FormData): string => {
		const labels = {
			username: 'Логин',
			email: 'Email',
			password: 'Пароль',
		}
		return labels[field]
	}

	const handleSubmit = async (event: React.FormEvent) => {
		event.preventDefault()

		if (!validateForm()) {
			return
		}

		setIsLoading(true)
		setError('')

		// Сохраняем данные компании
		const userData = {
			username: formData.username,
			email: formData.email,
			password: formData.password,
		}
		const token = localStorage.getItem('token')
		try {
			await api.post('/users', userData, {
				headers: {
					Authorization: token ? `Bearer ${token}` : '',
				},
			})

			enqueueSnackbar('Регистрация пользователя прошла успешно', {
				variant: 'success',
			})

			setFormData({
				username: '',
				email: '',
				password: '',
			})
		} catch (err: any) {
			setError(
				err.response?.data?.message ||
					'Ошибка при регистрации. Попробуйте позже.'
			)
		} finally {
			setIsLoading(false)
		}
	}

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
					}}
				>
					<Box sx={{ textAlign: 'center', mb: 4 }}>
						<Stack
							direction='row'
							alignItems='center'
							gap={3}
							justifyContent='center'
							sx={{ mb: 2 }}
						>
							{/* <DomainAddIcon sx={{ fontSize: '60px' }} color='primary' /> */}
							<Typography
								variant='h3'
								component='h1'
								gutterBottom
								sx={{
									m: 0,
									fontWeight: 'bold',
									background:
										'linear-gradient(135deg, #1976d2 0%, #42a5f5 100%)',
									backgroundClip: 'text',
									WebkitBackgroundClip: 'text',
									color: 'transparent',
								}}
							>
								Регистрация пользователя
							</Typography>
						</Stack>
						<Typography variant='body1' color='text.secondary'>
							Создайте аккаунт для участника вашей компании
						</Typography>
					</Box>
					{error && (
						<Alert severity='error' sx={{ mb: 3 }}>
							{error}
						</Alert>
					)}
					<Box component='form' onSubmit={handleSubmit}>
						<Stack spacing={3}>
							{/* Логин */}
							<TextField
								fullWidth
								label='Логин'
								name='username'
								value={formData.username}
								onChange={handleInputChange}
								required
								placeholder='fish_admin'
								error={error.includes('Логин')}
								helperText='Только латинские буквы, цифры и символ _'
							/>
							{/* Email */}
							<TextField
								fullWidth
								label='Email'
								name='email'
								type='email'
								value={formData.email}
								onChange={handleInputChange}
								required
								placeholder='admin@fishmaster.ru'
								error={error.includes('Email') || error.includes('email')}
							/>
							{/* Пароль */}
							<TextField
								fullWidth
								label='Пароль'
								name='password'
								type={showPassword ? 'text' : 'password'}
								value={formData.password}
								onChange={handleInputChange}
								required
								placeholder='Secret123!'
								error={error.includes('Пароль')}
								helperText='Минимум 6 символов'
								InputProps={{
									endAdornment: (
										<InputAdornment position='end'>
											<IconButton
												aria-label='toggle password visibility'
												onClick={handleClickShowPassword}
												edge='end'
											>
												{showPassword ? <VisibilityOff /> : <Visibility />}
											</IconButton>
										</InputAdornment>
									),
								}}
							/>
						</Stack>
						<Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 4 }}>
							<Button
								type='submit'
								variant='contained'
								disabled={isLoading}
								size='large'
							>
								{isLoading ? (
									<CircularProgress size={24} />
								) : (
									'Зарегистрировать участника'
								)}
							</Button>
						</Box>
					</Box>
				</Paper>
			</Container>
		</Fade>
	)
}

export default RegisterCompanyUser
