import React, { useState } from 'react'
import {
	Container,
	Typography,
	Box,
	Paper,
	TextField,
	Button,
	Alert,
	Stack,
	CircularProgress,
} from '@mui/material'
import { Phone, Email, LocationOn, Schedule, Send } from '@mui/icons-material'

const ContactPage: React.FC = () => {
	const [formData, setFormData] = useState({
		name: '',
		email: '',
		company: '',
		phone: '',
		message: '',
	})
	const [isSubmitting, setIsSubmitting] = useState(false)
	const [submitSuccess, setSubmitSuccess] = useState(false)

	const contactInfo = [
		{
			icon: <Phone sx={{ fontSize: 32, color: 'primary.main' }} />,
			title: 'Телефон',
			content: '+7 (999) 123-45-67',
			description: 'Пн-Пт с 9:00 до 18:00',
		},
		{
			icon: <Email sx={{ fontSize: 32, color: 'primary.main' }} />,
			title: 'Email',
			content: 'info@fishquota.ru',
			description: 'Отвечаем в течение 24 часов',
		},
		{
			icon: <LocationOn sx={{ fontSize: 32, color: 'primary.main' }} />,
			title: 'Адрес',
			content: 'г. Москва, ул. Рыбная, д. 15',
			description: 'Бизнес-центр "Морской"',
		},
		{
			icon: <Schedule sx={{ fontSize: 32, color: 'primary.main' }} />,
			title: 'Часы работы',
			content: 'Пн-Пт: 9:00-18:00',
			description: 'Сб-Вс: выходной',
		},
	]

	const handleInputChange =
		(field: string) => (event: React.ChangeEvent<HTMLInputElement>) => {
			setFormData(prev => ({ ...prev, [field]: event.target.value }))
		}

	const handleSubmit = async (event: React.FormEvent) => {
		event.preventDefault()
		setIsSubmitting(true)

		// Имитация отправки формы
		try {
			await new Promise(resolve => setTimeout(resolve, 1500))
			console.log('Данные формы:', formData)
			setSubmitSuccess(true)
			setFormData({
				name: '',
				email: '',
				company: '',
				phone: '',
				message: '',
			})
		} catch (error) {
			console.error('Ошибка отправки:', error)
		} finally {
			setIsSubmitting(false)
		}
	}

	const isFormValid = formData.name && formData.email && formData.message

	return (
		<Container sx={{ py: { xs: 4, md: 6 } }}>
			{/* Заголовок */}
			<Box sx={{ textAlign: 'center', mb: { xs: 4, md: 6 } }}>
				<Typography
					variant='h2'
					component='h1'
					sx={{
						fontWeight: 700,
						fontSize: { xs: '2.5rem', md: '3rem' },
						mb: 2,
					}}
				>
					Свяжитесь с нами
				</Typography>
				<Typography
					variant='h6'
					component='p'
					sx={{
						color: 'text.secondary',
						maxWidth: '600px',
						margin: '0 auto',
						fontSize: { xs: '1rem', md: '1.2rem' },
						lineHeight: 1.6,
					}}
				>
					Есть вопросы о нашей платформе? Мы всегда готовы помочь и ответить на
					все ваши вопросы о системе учёта рыболовных квот.
				</Typography>
			</Box>

			<Box
				sx={{
					display: 'flex',
					flexDirection: { xs: 'column', lg: 'row' },
					gap: 4,
				}}
			>
				{/* Контактная информация */}
				<Box sx={{ flex: 1 }}>
					<Paper
						sx={{
							p: 4,
							height: 'fit-content',
							borderRadius: 2,
							boxShadow: '0 4px 20px rgba(0,0,0,0.08)',
						}}
					>
						<Typography
							variant='h4'
							component='h2'
							sx={{ fontWeight: 600, mb: 3 }}
						>
							Контактная информация
						</Typography>

						<Stack spacing={3}>
							{contactInfo.map((item, index) => (
								<Box
									key={index}
									sx={{ display: 'flex', alignItems: 'flex-start', gap: 2 }}
								>
									<Box sx={{ mt: 0.5 }}>{item.icon}</Box>
									<Box>
										<Typography
											variant='h6'
											component='h3'
											sx={{ fontWeight: 600, mb: 0.5 }}
										>
											{item.title}
										</Typography>
										<Typography
											variant='body1'
											sx={{ fontWeight: 500, mb: 0.5 }}
										>
											{item.content}
										</Typography>
										<Typography variant='body2' color='text.secondary'>
											{item.description}
										</Typography>
									</Box>
								</Box>
							))}
						</Stack>

						<Box sx={{ mt: 4, p: 3, bgcolor: 'primary.50', borderRadius: 2 }}>
							<Typography variant='h6' sx={{ fontWeight: 600, mb: 1 }}>
								Техническая поддержка
							</Typography>
							<Typography variant='body2' color='text.secondary'>
								Для вопросов, связанных с техническими проблемами или
								функциональностью платформы, пишите на:{' '}
								<Box component='span' sx={{ fontWeight: 600 }}>
									support@fishquota.ru
								</Box>
							</Typography>
						</Box>
					</Paper>
				</Box>

				{/* Форма обратной связи */}
				<Box sx={{ flex: 1 }}>
					<Paper
						sx={{
							p: 4,
							borderRadius: 2,
							boxShadow: '0 4px 20px rgba(0,0,0,0.08)',
						}}
					>
						<Typography
							variant='h4'
							component='h2'
							sx={{ fontWeight: 600, mb: 3 }}
						>
							Напишите нам
						</Typography>

						{submitSuccess && (
							<Alert severity='success' sx={{ mb: 3 }}>
								Сообщение успешно отправлено! Мы свяжемся с вами в ближайшее
								время.
							</Alert>
						)}

						<Box component='form' onSubmit={handleSubmit}>
							<Stack spacing={3}>
								<Box
									sx={{
										display: 'flex',
										gap: 2,
										flexDirection: { xs: 'column', sm: 'row' },
									}}
								>
									<TextField
										label='Ваше имя *'
										value={formData.name}
										onChange={handleInputChange('name')}
										fullWidth
										required
									/>
									<TextField
										label='Email *'
										type='email'
										value={formData.email}
										onChange={handleInputChange('email')}
										fullWidth
										required
									/>
								</Box>

								<Box
									sx={{
										display: 'flex',
										gap: 2,
										flexDirection: { xs: 'column', sm: 'row' },
									}}
								>
									<TextField
										label='Компания'
										value={formData.company}
										onChange={handleInputChange('company')}
										fullWidth
										placeholder='Название вашей компании'
									/>
									<TextField
										label='Телефон'
										value={formData.phone}
										onChange={handleInputChange('phone')}
										fullWidth
										placeholder='+7 (999) 999-99-99'
									/>
								</Box>

								<TextField
									label='Сообщение *'
									value={formData.message}
									onChange={handleInputChange('message')}
									multiline
									rows={5}
									fullWidth
									required
									placeholder='Расскажите о вашем вопросе или проекте...'
								/>

								<Button
									type='submit'
									variant='contained'
									size='large'
									disabled={!isFormValid || isSubmitting}
									startIcon={
										isSubmitting ? <CircularProgress size={20} /> : <Send />
									}
									sx={{
										py: 1.5,
										fontSize: '1.1rem',
										fontWeight: 600,
										borderRadius: 2,
									}}
								>
									{isSubmitting ? 'Отправка...' : 'Отправить сообщение'}
								</Button>
							</Stack>
						</Box>

						<Typography
							variant='body2'
							color='text.secondary'
							sx={{ mt: 3, textAlign: 'center' }}
						>
							Нажимая кнопку, вы соглашаетесь с обработкой персональных данных
						</Typography>
					</Paper>
				</Box>
			</Box>

			{/* Дополнительная информация */}
			<Paper
				sx={{
					mt: 4,
					p: 4,
					bgcolor: 'primary.main',
					color: 'white',
					borderRadius: 2,
					textAlign: 'center',
				}}
			>
				<Typography variant='h5' component='h3' sx={{ fontWeight: 600, mb: 2 }}>
					Готовы начать работу?
				</Typography>
				<Typography
					variant='body1'
					sx={{ mb: 3, opacity: 0.9, maxWidth: '500px', margin: '0 auto' }}
				>
					Зарегистрируйте свою компанию в системе и начните эффективно управлять
					рыболовными квотами.
				</Typography>
				<Button
					variant='contained'
					size='large'
					href='/register'
					sx={{
						mt: 2,
						bgcolor: 'white',
						color: 'primary.main',
						px: 4,
						py: 1.5,
						fontWeight: 600,
						borderRadius: 2,
						'&:hover': {
							bgcolor: 'grey.100',
						},
					}}
				>
					Зарегистрировать компанию
				</Button>
			</Paper>
		</Container>
	)
}

export default ContactPage
