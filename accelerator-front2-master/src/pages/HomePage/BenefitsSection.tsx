import React, { useEffect } from 'react'
import { Box, Typography, Card, CardContent } from '@mui/material'
import { CheckCircle, Speed, Analytics, Security } from '@mui/icons-material'
import { motion, useAnimation } from 'framer-motion'
import { useInView } from 'react-intersection-observer'

const BenefitsSection: React.FC = () => {
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

	const benefits = [
		{
			icon: <CheckCircle sx={{ fontSize: 40, color: 'primary.main' }} />,
			title: 'Простота использования',
			description:
				'Интуитивный интерфейс для быстрого ввода данных и просмотра статистики',
			direction: 'left',
		},
		{
			icon: <Speed sx={{ fontSize: 40, color: 'primary.main' }} />,
			title: 'Экономия времени',
			description:
				'Автоматизация отчётности и контроль квот в реальном времени',
			direction: 'up',
		},
		{
			icon: <Analytics sx={{ fontSize: 40, color: 'primary.main' }} />,
			title: 'Детальная аналитика',
			description: 'Подробные отчёты и визуализация данных по уловам и квотам',
			direction: 'up',
		},
		{
			icon: <Security sx={{ fontSize: 40, color: 'primary.main' }} />,
			title: 'Надёжность',
			description:
				'Безопасное хранение данных и соответствие законодательным требованиям',
			direction: 'right',
		},
	]

	const containerVariants = {
		hidden: { opacity: 0 },
		visible: {
			opacity: 1,
			transition: {
				staggerChildren: 0.2,
				delayChildren: 0.1,
			},
		},
	}

	const cardVariants = {
		hidden: (direction: string) => {
			switch (direction) {
				case 'left':
					return { opacity: 0, x: -50 }
				case 'right':
					return { opacity: 0, x: 50 }
				case 'up':
					return { opacity: 0, y: 50 }
				default:
					return { opacity: 0, y: 50 }
			}
		},
		visible: {
			opacity: 1,
			x: 0,
			y: 0,
			transition: {
				type: 'spring',
				damping: 15,
				stiffness: 100,
				duration: 0.5,
			},
		},
	}

	return (
		<Box sx={{ textAlign: 'center' }} ref={ref}>
			{/* Заголовок секции */}
			<Typography
				variant='h3'
				component='h2'
				sx={{
					fontWeight: 700,
					mb: 2,
					fontSize: { xs: '2rem', md: '2.5rem' },
				}}
			>
				Почему выбирают нас
			</Typography>

			<Typography
				variant='h6'
				component='p'
				sx={{
					color: 'text.secondary',
					mb: 6,
					maxWidth: '600px',
					margin: '0 auto',
					fontSize: { xs: '1rem', md: '1.1rem' },
				}}
			>
				Современное решение для эффективного управления рыболовной деятельностью
			</Typography>

			{/* Карточки преимуществ */}
			<motion.div
				variants={containerVariants}
				initial='hidden'
				animate={controls}
				style={{ marginTop: '2rem' }}
			>
				<Box
					sx={{
						display: 'grid',
						gridTemplateColumns: {
							xs: '1fr',
							sm: '1fr 1fr',
							md: '1fr 1fr 1fr 1fr',
						},
						gap: 3,
					}}
				>
					{benefits.map((benefit, index) => (
						<motion.div
							key={index}
							variants={cardVariants}
							custom={benefit.direction}
							whileHover={{
								y: -8,
								transition: { type: 'spring', stiffness: 300 },
							}}
						>
							<Card
								elevation={3}
								sx={{
									borderRadius: 2,
									border: 'none',
									transition: 'box-shadow 0.3s ease-in-out',
									boxShadow: '0 4px 20px rgba(0,0,0,0.08)',
									'&:hover': {
										boxShadow: '0 8px 30px rgba(0,0,0,0.15)',
									},
									height: '100%',
									display: 'flex',
									flexDirection: 'column',
								}}
							>
								<CardContent sx={{ p: 3, textAlign: 'center', flexGrow: 1 }}>
									<Box
										sx={{
											mb: 2,
											display: 'flex',
											justifyContent: 'center',
											alignItems: 'center',
										}}
									>
										{benefit.icon}
									</Box>
									<Typography
										variant='h6'
										component='h3'
										sx={{
											fontWeight: 600,
											mb: 1,
											fontSize: { xs: '1rem', md: '1.1rem' },
										}}
									>
										{benefit.title}
									</Typography>
									<Typography
										variant='body2'
										color='text.secondary'
										sx={{ lineHeight: 1.5 }}
									>
										{benefit.description}
									</Typography>
								</CardContent>
							</Card>
						</motion.div>
					))}
				</Box>
			</motion.div>
		</Box>
	)
}

export default BenefitsSection
