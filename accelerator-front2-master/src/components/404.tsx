// pages/NotFoundPage/NotFoundPage.tsx
import {
	Box,
	Typography,
	Button,
	Container,
	Paper,
	Fade,
	Grow,
	keyframes,
	useTheme,
} from '@mui/material'
import {
	Home as HomeIcon,
	ArrowBack as ArrowBackIcon,
	Support as SupportIcon,
} from '@mui/icons-material'
import { useNavigate } from 'react-router-dom'

// Анимация плавающего эффекта
const float = keyframes`
  0% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
  100% {
    transform: translateY(0px);
  }
`

// Анимация мерцания
const blink = keyframes`
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
`

export default function NotFoundPage() {
	const navigate = useNavigate()
	const theme = useTheme()
	const isDark = theme.palette.mode === 'dark'

	return (
		<Fade in timeout={800}>
			<Container maxWidth='md'>
				<Box
					sx={{
						minHeight: '80vh',
						display: 'flex',
						flexDirection: 'column',
						alignItems: 'center',
						justifyContent: 'center',
						textAlign: 'center',
						py: 8,
						// background: isDark
						// 	? 'linear-gradient(180deg, #0a192f 0%, #1a1a2e 100%)'
						// 	: 'linear-gradient(180deg, #f8fafc 0%, #e2e8f0 100%)',
					}}
				>
					{/* Анимированное число 404 */}
					<Grow in timeout={1000}>
						<Box sx={{ position: 'relative', mb: 4 }}>
							<Typography
								variant='h1'
								sx={{
									fontSize: { xs: '8rem', sm: '12rem', md: '15rem' },
									fontWeight: 900,
									background: isDark
										? 'linear-gradient(45deg, #4f46e5, #7c3aed)'
										: 'linear-gradient(45deg, #3b82f6, #8b5cf6)',
									backgroundClip: 'text',
									WebkitBackgroundClip: 'text',
									color: 'transparent',
									animation: `${float} 3s ease-in-out infinite`,
								}}
							>
								404
							</Typography>

							{/* Декоративные элементы */}
							<Box
								sx={{
									position: 'absolute',
									top: -20,
									right: -20,
									width: 100,
									height: 100,
									borderRadius: '50%',
									background: isDark
										? 'radial-gradient(circle, rgba(79, 70, 229, 0.3) 0%, transparent 70%)'
										: 'radial-gradient(circle, rgba(59, 130, 246, 0.2) 0%, transparent 70%)',
									animation: `${blink} 2s ease-in-out infinite`,
								}}
							/>
							<Box
								sx={{
									position: 'absolute',
									bottom: -10,
									left: -30,
									width: 80,
									height: 80,
									borderRadius: '50%',
									background: isDark
										? 'radial-gradient(circle, rgba(124, 58, 237, 0.2) 0%, transparent 70%)'
										: 'radial-gradient(circle, rgba(139, 92, 246, 0.15) 0%, transparent 70%)',
									animation: `${blink} 3s ease-in-out infinite 0.5s`,
								}}
							/>
						</Box>
					</Grow>

					{/* Заголовок */}
					<Grow in timeout={1200}>
						<Typography
							variant='h3'
							gutterBottom
							sx={{
								fontWeight: 600,
								mb: 2,
								color: isDark ? '#f8fafc' : '#1e293b',
							}}
						>
							Страница не найдена
						</Typography>
					</Grow>

					{/* Описание */}
					<Grow in timeout={1400}>
						<Typography
							variant='h6'
							paragraph
							sx={{
								maxWidth: 600,
								mb: 4,
								color: isDark ? '#cbd5e1' : '#64748b',
							}}
						>
							Кажется, вы пытаетесь открыть страницу, которой не существует.
							Возможно, она была перемещена или удалена.
						</Typography>
					</Grow>

					{/* Карточка с полезной информацией */}
					<Fade in timeout={1600}>
						<Paper
							elevation={isDark ? 0 : 2}
							sx={{
								p: 3,
								mb: 4,
								maxWidth: 500,
								borderRadius: 2,
								background: isDark
									? 'rgba(30, 41, 59, 0.7)'
									: 'rgba(255, 255, 255, 0.9)',
								border: isDark
									? '1px solid rgba(255, 255, 255, 0.1)'
									: '1px solid rgba(0, 0, 0, 0.05)',
								backdropFilter: 'blur(10px)',
							}}
						>
							<Typography
								variant='body1'
								paragraph
								sx={{
									color: isDark ? '#e2e8f0' : '#334155',
									fontWeight: 500,
								}}
							>
								Что можно сделать:
							</Typography>
							<Box
								component='ul'
								sx={{
									textAlign: 'left',
									pl: 2,
									'& li': {
										mb: 1.5,
										color: isDark ? '#cbd5e1' : '#475569',
										display: 'flex',
										alignItems: 'center',
										gap: 1,
									},
								}}
							>
								<li>
									<Box
										sx={{
											width: 6,
											height: 6,
											borderRadius: '50%',
											background: isDark ? '#4f46e5' : '#3b82f6',
										}}
									/>
									Проверьте правильность введенного адреса
								</li>
								<li>
									<Box
										sx={{
											width: 6,
											height: 6,
											borderRadius: '50%',
											background: isDark ? '#7c3aed' : '#8b5cf6',
										}}
									/>
									Вернитесь на предыдущую страницу
								</li>
								<li>
									<Box
										sx={{
											width: 6,
											height: 6,
											borderRadius: '50%',
											background: isDark ? '#4f46e5' : '#3b82f6',
										}}
									/>
									Перейдите на главную страницу
								</li>
								<li>
									<Box
										sx={{
											width: 6,
											height: 6,
											borderRadius: '50%',
											background: isDark ? '#7c3aed' : '#8b5cf6',
										}}
									/>
									Свяжитесь с поддержкой
								</li>
							</Box>
						</Paper>
					</Fade>

					{/* Кнопки действий */}
					<Grow in timeout={1800}>
						<Box
							sx={{
								display: 'flex',
								gap: 2,
								flexWrap: 'wrap',
								justifyContent: 'center',
							}}
						>
							<Button
								variant='contained'
								size='large'
								startIcon={<ArrowBackIcon />}
								onClick={() => navigate(-1)}
								sx={{
									px: 4,
									py: 1.5,
									borderRadius: 2,
									// background: isDark
									// 	? 'linear-gradient(135deg, #4f46e5, #7c3aed)'
									// 	: 'linear-gradient(135deg, #3b82f6, #8b5cf6)',
									// '&:hover': {
									// 	background: isDark
									// 		? 'linear-gradient(135deg, #4338ca, #6d28d9)'
									// 		: 'linear-gradient(135deg, #2563eb, #7c3aed)',
									// 	transform: 'translateY(-2px)',
									// 	boxShadow: isDark
									// 		? '0 10px 25px rgba(79, 70, 229, 0.3)'
									// 		: '0 10px 25px rgba(59, 130, 246, 0.3)',
									// },
									transition: 'all 0.3s ease',
								}}
							>
								Назад
							</Button>

							<Button
								variant='outlined'
								size='large'
								startIcon={<HomeIcon />}
								onClick={() => navigate('/')}
								sx={{
									px: 4,
									py: 1.5,
									borderRadius: 2,
									color: isDark ? '#e2e8f0' : '#334155',
									borderColor: isDark
										? 'rgba(255, 255, 255, 0.2)'
										: 'rgba(0, 0, 0, 0.1)',
									'&:hover': {
										background: isDark
											? 'rgba(255, 255, 255, 0.05)'
											: 'rgba(0, 0, 0, 0.02)',
										borderColor: isDark ? '#4f46e5' : '#3b82f6',
										transform: 'translateY(-2px)',
									},
									transition: 'all 0.3s ease',
								}}
							>
								На главную
							</Button>

							<Button
								variant='text'
								size='large'
								startIcon={<SupportIcon />}
								onClick={() => navigate('/contact')}
								sx={{
									px: 4,
									py: 1.5,
									borderRadius: 2,
									color: isDark ? '#94a3b8' : '#64748b',
									'&:hover': {
										color: isDark ? '#e2e8f0' : '#334155',
										background: isDark
											? 'rgba(255, 255, 255, 0.03)'
											: 'rgba(0, 0, 0, 0.02)',
									},
									transition: 'all 0.3s ease',
								}}
							>
								Поддержка
							</Button>
						</Box>
					</Grow>

					{/* Дополнительная информация */}
					<Fade in timeout={2000}>
						<Typography
							variant='body2'
							sx={{
								mt: 6,
								color: isDark ? '#64748b' : '#94a3b8',
								maxWidth: 500,
							}}
						>
							Если вы считаете, что это ошибка, пожалуйста, сообщите нам об
							этом. Мы постараемся исправить проблему как можно скорее.
						</Typography>
					</Fade>

					{/* Декоративные элементы внизу */}
					<Fade in timeout={2200}>
						<Box
							sx={{
								mt: 6,
								display: 'flex',
								gap: 3,
								opacity: 0.6,
							}}
						>
							{[1, 2, 3, 4, 5].map(i => (
								<Box
									key={i}
									sx={{
										width: 8,
										height: 8,
										borderRadius: '50%',
										background: isDark
											? 'linear-gradient(45deg, #4f46e5, #7c3aed)'
											: 'linear-gradient(45deg, #3b82f6, #8b5cf6)',
										animation: `${float} 2s ease-in-out infinite ${i * 0.2}s`,
									}}
								/>
							))}
						</Box>
					</Fade>

					{/* Ссылка на помощь */}
					{/* <Fade in timeout={2400}>
						<Button
							variant='text'
							size='small'
							onClick={() => navigate('/help')}
							sx={{
								mt: 4,
								color: isDark ? '#4f46e5' : '#3b82f6',
								'&:hover': {
									background: 'transparent',
									textDecoration: 'underline',
								},
							}}
						>
							Нужна помощь? Посетите наш справочный центр
						</Button>
					</Fade> */}
				</Box>
			</Container>
		</Fade>
	)
}
