import React from 'react'
import {
	Container,
	Paper,
	Box,
	Button,
	Typography,
	Alert,
	CircularProgress,
	Divider,
	Stepper,
	Step,
	StepLabel,
	Stack,
} from '@mui/material'
import { ArrowBack } from '@mui/icons-material'
import { useNavigate, Link } from 'react-router-dom'
import CompanyInfoStep from './steps/CompanyInfoStep'
import AddressStep from './steps/AddressStep'
import CredentialsStep from './steps/CredentialsStep'
import { useRegisterForm } from '../../hooks/useRegisterForm'
import DomainAddIcon from '@mui/icons-material/DomainAdd'

const RegisterPage: React.FC = () => {
	const navigate = useNavigate()
	const {
		activeStep,
		formData,
		isLoading,
		error,
		handleInputChange,
		handleCheckboxChange,
		handleMultiSelectChange,
		validateStep,
		setActiveStep,
		setIsLoading,
		setError,
	} = useRegisterForm()

	const steps = ['Информация о компании', 'Адрес и контакты', 'Учетные данные']

	const handleNext = () => {
		if (!validateStep(activeStep)) {
			return
		}
		setActiveStep(prev => prev + 1)
		setError('')
	}

	const handleBack = () => {
		setActiveStep(prev => prev - 1)
		setError('')
	}

	const handleSubmit = async (event: React.FormEvent) => {
		event.preventDefault()

		if (!validateStep(activeStep)) {
			return
		}

		setIsLoading(true)
		setError('')

		try {
			await new Promise(resolve => setTimeout(resolve, 2000))
			console.log('Регистрация компании:', formData)

			const companyData = {
				companyName: formData.companyName,
				inn: formData.inn,
				email: formData.email,
				username: formData.username,
				role: 'company_admin',
				fullName: formData.fullName,
				position: formData.position,
			}

			localStorage.setItem('company', JSON.stringify(companyData))
			localStorage.setItem('user', JSON.stringify(companyData))
			navigate('/company/dashboard')
		} catch {
			setError('Ошибка при регистрации. Попробуйте позже.')
		} finally {
			setIsLoading(false)
		}
	}

	const getStepContent = (step: number) => {
		switch (step) {
			case 0:
				return (
					<CompanyInfoStep
						formData={formData}
						onInputChange={handleInputChange}
					/>
				)
			case 1:
				return (
					<AddressStep
						formData={formData}
						onInputChange={handleInputChange}
						onMultiSelectChange={handleMultiSelectChange}
					/>
				)
			case 2:
				return (
					<CredentialsStep
						formData={formData}
						onInputChange={handleInputChange}
						onCheckboxChange={handleCheckboxChange}
					/>
				)
			default:
				return 'Неизвестный шаг'
		}
	}

	return (
		<Container
			maxWidth='lg'
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
					borderRadius: 3,
				}}
			>
				<Box sx={{ textAlign: 'center', mb: 4 }}>
					<Stack
						direction='row'
						alignItems='center'
						gap={3}
						justifyContent='center'
					>
						<DomainAddIcon sx={{ fontSize: '60px' }} color='primary' />
						<Typography
							variant='h3'
							component='h1'
							gutterBottom
							sx={{
								m: 0,
								fontWeight: 'bold',
								background: 'linear-gradient(135deg, #1976d2 0%, #42a5f5 100%)',
								backgroundClip: 'text',
								WebkitBackgroundClip: 'text',
								color: 'transparent',
							}}
						>
							Регистрация компании
						</Typography>
					</Stack>
					<Typography variant='body1' color='text.secondary'>
						Создайте аккаунт для вашей рыболовной компании
					</Typography>
				</Box>

				<Stepper activeStep={activeStep} sx={{ mb: 4 }}>
					{steps.map(label => (
						<Step key={label}>
							<StepLabel>{label}</StepLabel>
						</Step>
					))}
				</Stepper>

				{error && (
					<Alert severity='error' sx={{ mb: 3 }}>
						{error}
					</Alert>
				)}

				<Box
					component='form'
					onSubmit={activeStep === steps.length - 1 ? handleSubmit : undefined}
				>
					{getStepContent(activeStep)}

					<Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 4 }}>
						<Button
							onClick={handleBack}
							disabled={activeStep === 0}
							startIcon={<ArrowBack />}
						>
							Назад
						</Button>

						{activeStep === steps.length - 1 ? (
							<Button
								type='submit'
								variant='contained'
								disabled={isLoading}
								size='large'
							>
								{isLoading ? (
									<CircularProgress size={24} />
								) : (
									'Зарегистрировать компанию'
								)}
							</Button>
						) : (
							<Button variant='contained' onClick={handleNext} size='large'>
								Далее
							</Button>
						)}
					</Box>
				</Box>

				<Divider sx={{ my: 4 }}>
					<Typography variant='body2' color='text.secondary'>
						Уже есть аккаунт?
					</Typography>
				</Divider>

				<Box sx={{ textAlign: 'center' }}>
					<Button component={Link} to='/login' variant='outlined' fullWidth>
						Войти в существующий аккаунт
					</Button>
				</Box>

				<Box sx={{ mt: 3, textAlign: 'center' }}>
					<Typography variant='caption' color='text.secondary'>
						После регистрации вы получите роль "Администратор компании". Для
						подтверждения регистрации может потребоваться проверка документов.
					</Typography>
				</Box>
			</Paper>
		</Container>
	)
}

export default RegisterPage
