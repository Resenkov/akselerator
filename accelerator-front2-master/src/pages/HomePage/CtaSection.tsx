import React, { useEffect } from 'react'
import { Box, Typography, Button, Container, Paper } from '@mui/material'
import { useNavigate } from 'react-router-dom'
import { motion, useAnimation } from 'framer-motion'
import { useInView } from 'react-intersection-observer'
import { ArrowForward, TouchApp, Rocket } from '@mui/icons-material'

const CtaSection: React.FC = () => {
	const navigate = useNavigate()
	const controls = useAnimation()
	const [ref, inView] = useInView({
		threshold: 0.1,
		triggerOnce: true,
	})

	useEffect(() => {
		if (inView) {
			controls.start('visible')
		}
	}, [controls, inView])

	const containerVariants = {
		hidden: { opacity: 0 },
		visible: {
			opacity: 1,
			transition: {
				staggerChildren: 0.3,
				delayChildren: 0.2,
			},
		},
	}

	const textVariants = {
		hidden: { opacity: 0, y: 20 },
		visible: {
			opacity: 1,
			y: 0,
			transition: {
				type: 'spring',
				stiffness: 100,
				damping: 15,
			},
		},
	}

	const buttonVariants = {
		hidden: { opacity: 0, scale: 0.9 },
		visible: {
			opacity: 1,
			scale: 1,
			transition: {
				type: 'spring',
				stiffness: 200,
				damping: 20,
			},
		},
		hover: {
			scale: 1.05,
			transition: {
				type: 'spring',
				stiffness: 400,
				damping: 25,
			},
		},
		tap: {
			scale: 0.95,
		},
	}

	const floatingIconVariants = {
		animate: {
			y: [0, -10, 0],
			transition: {
				duration: 3,
				repeat: Infinity,
				ease: 'easeInOut',
			},
		},
	}

	return (
		<Box ref={ref} sx={{ position: 'relative', overflow: 'hidden' }}>
			{/* Декоративные элементы */}
			<motion.div
				variants={floatingIconVariants}
				animate='animate'
				style={{
					position: 'absolute',
					top: '20%',
					left: '10%',
					opacity: 0.1,
					zIndex: 1,
				}}
			>
				<FishIcon />
			</motion.div>
			<motion.div
				variants={floatingIconVariants}
				animate='animate'
				transition={{ delay: 1 }}
				style={{
					position: 'absolute',
					bottom: '20%',
					right: '10%',
					opacity: 0.1,
					zIndex: 1,
				}}
			>
				<WaveIcon />
			</motion.div>

			<Paper
				elevation={0}
				sx={{
					position: 'relative',
					zIndex: 2,
					background:
						'linear-gradient(135deg, #1976d2 0%, #1565c0 50%, #0d47a1 100%)',
					color: 'white',
					py: { xs: 8, md: 12 },
					borderRadius: 4,
					textAlign: 'center',
					overflow: 'hidden',
					'&::before': {
						content: '""',
						position: 'absolute',
						top: 0,
						left: 0,
						right: 0,
						height: '4px',
						background: 'linear-gradient(90deg, #42a5f5, #bbdefb, #42a5f5)',
						animation: 'shimmer 3s infinite linear',
					},
					'@keyframes shimmer': {
						'0%': { transform: 'translateX(-100%)' },
						'100%': { transform: 'translateX(100%)' },
					},
				}}
			>
				{/* Волнистый узор */}
				<Box
					sx={{
						position: 'absolute',
						top: 0,
						left: 0,
						right: 0,
						height: '100%',
						backgroundImage: `url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M11 18c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm48 25c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm-43-7c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm63 31c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM34 90c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm56-76c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM12 86c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm28-65c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm23-11c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-6 60c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm29 22c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zM32 63c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm57-13c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-9-21c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM60 91c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM35 41c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM12 60c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2z' fill='%2342a5f5' fill-opacity='0.1' fill-rule='evenodd'/%3E%3C/svg%3E")`,
						opacity: 0.3,
						zIndex: 1,
					}}
				/>

				<Container maxWidth='md' sx={{ position: 'relative', zIndex: 3 }}>
					<motion.div
						variants={containerVariants}
						initial='hidden'
						animate={controls}
					>
						<motion.div variants={textVariants}>
							<Typography
								variant='h3'
								component='h2'
								sx={{
									fontWeight: 800,
									mb: 3,
									fontSize: { xs: '2rem', md: '3rem' },
									background: 'linear-gradient(45deg, #ffffff, #bbdefb)',
									backgroundClip: 'text',
									WebkitBackgroundClip: 'text',
									WebkitTextFillColor: 'transparent',
									textShadow: '0 2px 10px rgba(0,0,0,0.1)',
								}}
							>
								Готовы начать?
							</Typography>

							<Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
								<Rocket sx={{ fontSize: 40, mr: 2, color: '#bbdefb' }} />
								<TouchApp sx={{ fontSize: 40, color: '#bbdefb' }} />
							</Box>

							<Typography
								variant='h6'
								component='p'
								sx={{
									opacity: 0.95,
									mb: 5,
									maxWidth: '700px',
									margin: '0 auto',
									fontSize: { xs: '1.1rem', md: '1.3rem' },
									lineHeight: 1.7,
									fontWeight: 300,
								}}
							>
								Присоединяйтесь к <strong>лидерам рыболовной индустрии</strong>{' '}
								и используйте современные инструменты для эффективного
								управления вашей деятельностью. Начните работу уже сегодня!
							</Typography>
						</motion.div>

						<Box
							mt={6}
							sx={{
								display: 'flex',
								gap: 3,
								justifyContent: 'center',
								alignItems: 'center',
								flexWrap: 'wrap',
							}}
						>
							<motion.div
								variants={buttonVariants}
								whileHover='hover'
								whileTap='tap'
							>
								<Button
									variant='contained'
									size='large'
									onClick={() => navigate('/register')}
									startIcon={<ArrowForward />}
									sx={{
										background: 'linear-gradient(45deg, #ffffff, #e3f2fd)',
										color: '#1565c0',
										px: 5,
										py: 2,
										fontSize: '1.1rem',
										fontWeight: 700,
										borderRadius: 3,
										boxShadow: '0 8px 25px rgba(33, 150, 243, 0.4)',
										'&:hover': {
											background: 'linear-gradient(45deg, #ffffff, #f3f9ff)',
											boxShadow: '0 12px 35px rgba(33, 150, 243, 0.6)',
										},
									}}
								>
									Бесплатная регистрация
								</Button>
							</motion.div>

							<motion.div
								variants={buttonVariants}
								whileHover='hover'
								whileTap='tap'
								style={{ transitionDelay: '0.1s' }}
							>
								<Button
									variant='outlined'
									size='large'
									onClick={() => navigate('/contact')}
									sx={{
										border: '2px solid rgba(255,255,255,0.6)',
										color: 'white',
										px: 5,
										py: 2,
										fontSize: '1.1rem',
										fontWeight: 600,
										borderRadius: 3,
										backdropFilter: 'blur(10px)',
										'&:hover': {
											borderColor: 'white',
											backgroundColor: 'rgba(255,255,255,0.15)',
											backdropFilter: 'blur(20px)',
										},
									}}
								>
									Получить консультацию
								</Button>
							</motion.div>
						</Box>

						<motion.div
							initial={{ opacity: 0, scale: 0.5 }}
							animate={{ opacity: 1, scale: 1 }}
							transition={{ delay: 1, duration: 0.5 }}
						>
							<Typography
								variant='body2'
								sx={{
									mt: 6,
									opacity: 0.8,
									fontSize: '0.9rem',
									display: 'flex',
									alignItems: 'center',
									justifyContent: 'center',
									gap: 1,
								}}
							>
								<Box
									component='span'
									sx={{
										width: '8px',
										height: '8px',
										bgcolor: '#4caf50',
										borderRadius: '50%',
										animation: 'pulse 2s infinite',
										'@keyframes pulse': {
											'0%': { opacity: 1, transform: 'scale(1)' },
											'50%': { opacity: 0.5, transform: 'scale(1.2)' },
											'100%': { opacity: 1, transform: 'scale(1)' },
										},
									}}
								/>
								Первый месяц использования — бесплатно
							</Typography>
						</motion.div>
					</motion.div>
				</Container>
			</Paper>
		</Box>
	)
}

// Декоративные иконки
const FishIcon = () => (
	<svg width='60' height='60' viewBox='0 0 60 60' fill='none'>
		<path
			d='M30 15C25 10 15 10 10 15C5 20 5 30 10 35C15 40 25 40 30 35C35 40 45 40 50 35C55 30 55 20 50 15C45 10 35 10 30 15Z'
			fill='#42a5f5'
			fillOpacity='0.2'
		/>
		<path
			d='M25 25C27.5 22.5 32.5 22.5 35 25'
			stroke='#42a5f5'
			strokeWidth='2'
			strokeLinecap='round'
		/>
	</svg>
)

const WaveIcon = () => (
	<svg width='80' height='40' viewBox='0 0 80 40' fill='none'>
		<path
			d='M0 20C10 15 20 25 30 20C40 15 50 25 60 20C70 15 80 25 80 20V40H0V20Z'
			fill='#42a5f5'
			fillOpacity='0.2'
		/>
	</svg>
)

export default CtaSection
