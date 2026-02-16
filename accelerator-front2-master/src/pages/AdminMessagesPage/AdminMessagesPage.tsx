/* eslint-disable @typescript-eslint/no-explicit-any */
import React, { useState } from 'react'
import {
	Container,
	Typography,
	Box,
	Paper,
	Stack,
	Chip,
	Avatar,
	Divider,
	IconButton,
	Alert,
	Badge,
	Button,
	TextField,
} from '@mui/material'
import {
	MarkEmailRead,
	Person,
	Business,
	Search,
	Refresh,
	Delete,
	Email,
	Phone,
} from '@mui/icons-material'

// Mock данные сообщений
const initialMessages = [
	{
		id: 1,
		user: {
			name: 'Иван Петров',
			email: 'ivan@example.com',
			company: 'ООО "Рыбпром"',
			phone: '+7 (999) 123-45-67',
		},
		message:
			'Здравствуйте! У нас возник вопрос по распределению квот на хамсу в Азовском море. Можно ли увеличить лимит для нашей компании? Мы готовы предоставить дополнительную документацию при необходимости.',
		date: '2024-01-15 14:30',
		status: 'new',
	},
	{
		id: 2,
		user: {
			name: 'Анна Сидорова',
			email: 'anna@example.com',
			company: 'ИП Сидоров А.В.',
			phone: '+7 (999) 234-56-78',
		},
		message:
			'Не могу добавить улов за вчерашний день. Система выдает ошибку при сохранении. Что делать? Пробовал разные браузеры, проблема повторяется.',
		date: '2024-01-14 11:20',
		status: 'new',
	},
	{
		id: 3,
		user: {
			name: 'Петр Васильев',
			email: 'petr@example.com',
			company: 'АО "Морские ресурсы"',
			phone: '+7 (999) 345-67-89',
		},
		message:
			'Интересует возможность интеграции с нашей внутренней системой учета через API. Какие есть возможности? Какие форматы данных поддерживаются?',
		date: '2024-01-13 16:45',
		status: 'resolved',
	},
	{
		id: 4,
		user: {
			name: 'Мария Козлова',
			email: 'maria@example.com',
			company: 'ООО "Черноморрыба"',
			phone: '+7 (999) 456-78-90',
		},
		message:
			'Нужна помощь в формировании квартального отчета. Какие данные необходимо предоставить? Есть ли готовые шаблоны для заполнения?',
		date: '2024-01-15 10:00',
		status: 'new',
	},
]

const AdminMessagesPage: React.FC = () => {
	const [messages, setMessages] = useState(initialMessages)
	const [selectedMessage, setSelectedMessage] = useState<number | null>(1)
	const [searchTerm, setSearchTerm] = useState('')

	const statusColors = {
		new: 'primary',
		in_progress: 'warning',
		resolved: 'success',
	}

	const statusLabels = {
		new: 'Новое',
		in_progress: 'В работе',
		resolved: 'Решено',
	}

	const filteredMessages = messages.filter(
		message =>
			message.user.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
			message.user.company.toLowerCase().includes(searchTerm.toLowerCase()) ||
			message.message.toLowerCase().includes(searchTerm.toLowerCase())
	)

	const selectedMessageData = messages.find(msg => msg.id === selectedMessage)

	const handleMarkAsResolved = (messageId: number) => {
		setMessages(prev =>
			prev.map(msg =>
				msg.id === messageId ? { ...msg, status: 'resolved' } : msg
			)
		)
	}

	const handleDeleteMessage = (messageId: number) => {
		if (window.confirm('Вы уверены, что хотите удалить это сообщение?')) {
			setMessages(prev => prev.filter(msg => msg.id !== messageId))
			if (selectedMessage === messageId) {
				setSelectedMessage(filteredMessages[0]?.id || null)
			}
		}
	}

	const getUnreadCount = () => {
		return messages.filter(msg => msg.status === 'new').length
	}

	const handleReplyByEmail = (email: string) => {
		window.open(`mailto:${email}`, '_blank')
	}

	return (
		<Container maxWidth='xl' sx={{ py: 4 }}>
			{/* Заголовок */}
			<Box
				sx={{
					display: 'flex',
					justifyContent: 'space-between',
					alignItems: 'flex-start',
					mb: 4,
				}}
			>
				<Box>
					<Typography
						variant='h3'
						component='h1'
						sx={{ fontWeight: 700, mb: 1 }}
					>
						Сообщения от пользователей
					</Typography>
					<Typography variant='h6' color='text.secondary'>
						Контактные данные для ответа по почте
					</Typography>
				</Box>
				<Badge badgeContent={getUnreadCount()} color='error'>
					<Chip
						icon={<MarkEmailRead />}
						label={`${getUnreadCount()} новых`}
						color='primary'
						variant='outlined'
					/>
				</Badge>
			</Box>

			<Box
				sx={{
					display: 'flex',
					gap: 4,
					flexDirection: { xs: 'column', lg: 'row' },
				}}
			>
				{/* Список сообщений */}
				<Paper
					sx={{
						flex: 1,
						p: 3,
						borderRadius: 2,
						maxHeight: '80vh',
						overflow: 'auto',
					}}
				>
					<Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
						<TextField
							placeholder='Поиск по сообщениям...'
							value={searchTerm}
							onChange={e => setSearchTerm(e.target.value)}
							InputProps={{
								startAdornment: (
									<Search sx={{ color: 'text.secondary', mr: 1 }} />
								),
							}}
							sx={{ flex: 1 }}
						/>
						{/* <IconButton>
							<Refresh />
						</IconButton> */}
					</Box>

					<Stack spacing={2}>
						{filteredMessages.map(message => (
							<Paper
								key={message.id}
								sx={{
									p: 2,
									border: selectedMessage === message.id ? 2 : 1,
									borderColor:
										selectedMessage === message.id ? 'primary.main' : 'divider',
									cursor: 'pointer',
									transition: 'all 0.2s ease-in-out',
									'&:hover': {
										borderColor: 'primary.main',
										boxShadow: 2,
									},
								}}
								onClick={() => setSelectedMessage(message.id)}
							>
								<Box
									sx={{
										display: 'flex',
										justifyContent: 'space-between',
										alignItems: 'flex-start',
										mb: 1,
									}}
								>
									<Box
										sx={{
											display: 'flex',
											alignItems: 'center',
											gap: 1,
											mb: 1,
										}}
									>
										<Avatar sx={{ width: 32, height: 32 }}>
											<Person />
										</Avatar>
										<Box>
											<Typography variant='subtitle1' sx={{ fontWeight: 600 }}>
												{message.user.name}
											</Typography>
											<Typography variant='body2' color='text.secondary'>
												{message.user.company}
											</Typography>
										</Box>
									</Box>
									<Chip
										label={
											statusLabels[message.status as keyof typeof statusLabels]
										}
										color={
											statusColors[
												message.status as keyof typeof statusColors
											] as any
										}
										size='small'
									/>
								</Box>

								<Typography
									variant='body2'
									color='text.secondary'
									sx={{
										mb: 2,
										display: '-webkit-box',
										WebkitLineClamp: 2,
										WebkitBoxOrient: 'vertical',
										overflow: 'hidden',
									}}
								>
									{message.message}
								</Typography>

								<Box
									sx={{
										display: 'flex',
										justifyContent: 'space-between',
										alignItems: 'center',
									}}
								>
									<Typography variant='caption' color='text.secondary'>
										{message.date}
									</Typography>
								</Box>
							</Paper>
						))}

						{filteredMessages.length === 0 && (
							<Alert severity='info'>Сообщения не найдены</Alert>
						)}
					</Stack>
				</Paper>

				{/* Детали сообщения */}
				<Paper sx={{ flex: 2, p: 3, borderRadius: 2 }}>
					{selectedMessageData ? (
						<>
							{/* Заголовок сообщения */}
							<Box
								sx={{
									display: 'flex',
									justifyContent: 'space-between',
									alignItems: 'flex-start',
									mb: 3,
								}}
							>
								<Box>
									<Box
										sx={{
											display: 'flex',
											alignItems: 'center',
											gap: 2,
											mb: 1,
										}}
									>
										<Avatar sx={{ width: 48, height: 48 }}>
											<Business />
										</Avatar>
										<Box>
											<Typography variant='h5' sx={{ fontWeight: 600 }}>
												{selectedMessageData.user.name}
											</Typography>
											<Typography variant='body1' color='text.secondary'>
												{selectedMessageData.user.company}
											</Typography>
										</Box>
									</Box>
								</Box>
								<Box sx={{ display: 'flex', gap: 1 }}>
									{/* <Button
										variant='contained'
										startIcon={<Email />}
										onClick={() =>
											handleReplyByEmail(selectedMessageData.user.email)
										}
										sx={{ mr: 1 }}
									>
										Ответить
									</Button> */}
									<IconButton
										onClick={() => handleMarkAsResolved(selectedMessageData.id)}
										color='success'
										disabled={selectedMessageData.status === 'resolved'}
									>
										<MarkEmailRead />
									</IconButton>
									<IconButton
										onClick={() => handleDeleteMessage(selectedMessageData.id)}
										color='error'
									>
										<Delete />
									</IconButton>
								</Box>
							</Box>

							<Divider sx={{ my: 3 }} />

							{/* Контактная информация */}
							<Box sx={{ mb: 4 }}>
								<Typography variant='h6' sx={{ fontWeight: 600, mb: 2 }}>
									Контактные данные:
								</Typography>
								<Paper variant='outlined' sx={{ p: 3 }}>
									<Stack spacing={2}>
										<Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
											<Person color='primary' />
											<Box>
												<Typography variant='body2' color='text.secondary'>
													Имя
												</Typography>
												<Typography variant='body1' sx={{ fontWeight: 500 }}>
													{selectedMessageData.user.name}
												</Typography>
											</Box>
										</Box>
										<Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
											<Email color='primary' />
											<Box>
												<Typography variant='body2' color='text.secondary'>
													Email
												</Typography>
												<Typography variant='body1' sx={{ fontWeight: 500 }}>
													{selectedMessageData.user.email}
												</Typography>
											</Box>
										</Box>
										<Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
											<Business color='primary' />
											<Box>
												<Typography variant='body2' color='text.secondary'>
													Компания
												</Typography>
												<Typography variant='body1' sx={{ fontWeight: 500 }}>
													{selectedMessageData.user.company}
												</Typography>
											</Box>
										</Box>
										<Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
											<Phone color='primary' />
											<Box>
												<Typography variant='body2' color='text.secondary'>
													Телефон
												</Typography>
												<Typography variant='body1' sx={{ fontWeight: 500 }}>
													{selectedMessageData.user.phone}
												</Typography>
											</Box>
										</Box>
									</Stack>
								</Paper>
							</Box>

							{/* Сообщение пользователя */}
							<Box sx={{ mb: 4 }}>
								<Typography variant='h6' sx={{ fontWeight: 600, mb: 2 }}>
									Сообщение:
								</Typography>
								<Paper variant='outlined' sx={{ p: 3 }}>
									<Typography
										variant='body1'
										sx={{ whiteSpace: 'pre-wrap', lineHeight: 1.6 }}
									>
										{selectedMessageData.message}
									</Typography>
									<Typography
										variant='caption'
										color='text.secondary'
										sx={{ mt: 2, display: 'block' }}
									>
										Отправлено: {selectedMessageData.date}
									</Typography>
								</Paper>
							</Box>

							{/* Кнопка ответа */}
							{/* <Box sx={{ display: 'flex', justifyContent: 'center' }}>
								<Button
									variant='contained'
									size='large'
									startIcon={<Email />}
									onClick={() =>
										handleReplyByEmail(selectedMessageData.user.email)
									}
									sx={{ px: 4, py: 1.5 }}
								>
									Ответить по почте
								</Button>
							</Box> */}
						</>
					) : (
						<Box sx={{ textAlign: 'center', py: 8 }}>
							<MarkEmailRead
								sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }}
							/>
							<Typography variant='h6' color='text.secondary'>
								Выберите сообщение для просмотра
							</Typography>
						</Box>
					)}
				</Paper>
			</Box>
		</Container>
	)
}

export default AdminMessagesPage
