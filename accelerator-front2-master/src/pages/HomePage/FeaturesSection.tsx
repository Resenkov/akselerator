import React, { useEffect } from 'react'
import { Box, Typography, Paper } from '@mui/material'
import { motion, useAnimation } from 'framer-motion'
import { useInView } from 'react-intersection-observer'

const FeaturesSection: React.FC = () => {
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

	const features = [
		{
			number: '01',
			title: 'Учёт уловов',
			description:
				'Простой и быстрый ввод данных о пойманной рыбе с автоматической проверкой квот',
			path: '/catch',
		},
		{
			number: '02',
			title: 'Контроль квот',
			description:
				'Мониторинг использования квот в реальном времени с уведомлениями о приближении лимитов',
			path: '/quotas',
		},
		{
			number: '03',
			title: 'Аналитика и отчёты',
			description:
				'Детальные отчёты и визуализация данных для принятия взвешенных решений по управлению рыболовной деятельностью',
			path: '/analytics',
		},
	]

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

	const headerVariants = {
		hidden: { opacity: 0, y: -30 },
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

	const featureVariants = {
		hidden: {
			opacity: 0,
			y: 50,
			scale: 0.95,
		},
		visible: {
			opacity: 1,
			y: 0,
			scale: 1,
			transition: {
				type: 'spring',
				stiffness: 100,
				damping: 20,
				duration: 0.6,
			},
		},
		hover: {
			scale: 1.02,
			transition: {
				type: 'spring',
				stiffness: 400,
				damping: 25,
			},
		},
		tap: {
			scale: 0.98,
		},
	}

	return (
		<Box sx={{ textAlign: 'center' }} ref={ref}>
			<motion.div
				variants={containerVariants}
				initial='hidden'
				animate={controls}
			>
				<motion.div variants={headerVariants}>
					<Typography
						variant='h3'
						component='h2'
						sx={{
							fontWeight: 700,
							mb: 2,
							fontSize: { xs: '2rem', md: '2.5rem' },
						}}
					>
						Ключевые возможности
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
						Все необходимые инструменты для эффективного управления рыболовной
						деятельностью
					</Typography>
				</motion.div>

				{/* Первая строка - 2 пункта */}
				<Box
					mt={4}
					sx={{
						display: 'flex',
						gap: 4,
						mb: 4,
						flexDirection: { xs: 'column', md: 'row' },
					}}
				>
					{features.slice(0, 2).map((feature, index) => (
						<motion.div
							key={index}
							variants={featureVariants}
							whileHover='hover'
							whileTap='tap'
							style={{ flex: 1 }}
						>
							<Paper
								elevation={3}
								sx={{
									display: 'flex',
									alignItems: 'flex-start',
									gap: 3,
									textAlign: 'left',
									p: 4,
									borderRadius: 2,
									cursor: 'pointer',
									// border: '1px solid',
									// borderColor: 'divider',
									height: '100%',
								}}
							>
								<motion.div
									initial={{ scale: 0.8, opacity: 0 }}
									animate={{ scale: 1, opacity: 1 }}
									transition={{ delay: 0.5 + index * 0.2, type: 'spring' }}
								>
									<Typography
										variant='h3'
										component='div'
										sx={{
											fontSize: { xs: '2.5rem', md: '3rem' },
											fontWeight: 700,
											color: 'primary.main',
											lineHeight: 1,
											minWidth: '70px',
										}}
									>
										{feature.number}
									</Typography>
								</motion.div>
								<Box sx={{ flex: 1 }}>
									<Typography
										variant='h5'
										component='h3'
										sx={{
											fontWeight: 600,
											mb: 2,
											fontSize: { xs: '1.3rem', md: '1.5rem' },
										}}
									>
										{feature.title}
									</Typography>
									<Typography
										variant='body1'
										color='text.secondary'
										sx={{ lineHeight: 1.6, mb: 2 }}
									>
										{feature.description}
									</Typography>
								</Box>
							</Paper>
						</motion.div>
					))}
				</Box>

				{/* Вторая строка - 1 пункт по центру */}
				<Box
					sx={{
						display: 'flex',
						justifyContent: 'center',
					}}
				>
					<motion.div
						variants={featureVariants}
						whileHover='hover'
						whileTap='tap'
						style={{ maxWidth: '600px', width: '100%' }}
					>
						<Paper
							elevation={3}
							sx={{
								display: 'flex',
								alignItems: 'flex-start',
								gap: 3,
								textAlign: 'left',
								p: 4,
								borderRadius: 2,
								cursor: 'pointer',
								border: '1px solid',
								borderColor: 'divider',
							}}
						>
							<motion.div
								initial={{ scale: 0.8, opacity: 0 }}
								animate={{ scale: 1, opacity: 1 }}
								transition={{ delay: 0.8, type: 'spring' }}
							>
								<Typography
									variant='h3'
									component='div'
									sx={{
										fontSize: { xs: '2.5rem', md: '3rem' },
										fontWeight: 700,
										color: 'primary.main',
										lineHeight: 1,
										minWidth: '70px',
									}}
								>
									{features[2].number}
								</Typography>
							</motion.div>
							<Box sx={{ flex: 1 }}>
								<Typography
									variant='h5'
									component='h3'
									sx={{
										fontWeight: 600,
										mb: 2,
										fontSize: { xs: '1.3rem', md: '1.5rem' },
									}}
								>
									{features[2].title}
								</Typography>
								<Typography
									variant='body1'
									color='text.secondary'
									sx={{ lineHeight: 1.6, mb: 2 }}
								>
									{features[2].description}
								</Typography>
							</Box>
						</Paper>
					</motion.div>
				</Box>
			</motion.div>
		</Box>
	)
}

export default FeaturesSection
