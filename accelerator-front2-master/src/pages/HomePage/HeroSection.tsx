import React from 'react'
import { Box, Typography, Button, Container } from '@mui/material'
import { useNavigate } from 'react-router-dom'

const HeroSection: React.FC = () => {
	const navigate = useNavigate()

	return (
		<Box
			sx={{
				// minHeight: '80vh',
				display: 'flex',
				alignItems: 'center',
				justifyContent: 'center',
				textAlign: 'center',
				// py: 8,
			}}
		>
			<Container>
				{/* Заголовок */}
				<Typography
					variant='h1'
					component='h1'
					sx={{
						fontSize: { xs: '2.5rem', md: '3.5rem', lg: '4rem' },
						fontWeight: 700,
						lineHeight: 1.1,
						mb: 3,
						background: 'linear-gradient(135deg, #1976d2 0%, #42a5f5 100%)',
						backgroundClip: 'text',
						WebkitBackgroundClip: 'text',
						color: 'transparent',
					}}
				>
					Цифровая платформа
					<br />
					для учёта рыболовных квот
				</Typography>

				{/* Подзаголовок */}
				<Typography
					variant='h5'
					component='p'
					sx={{
						fontSize: { xs: '1.1rem', md: '1.3rem' },
						fontWeight: 400,
						color: 'text.secondary',
						mb: 4,
						maxWidth: '600px',
						margin: '0 auto',
						lineHeight: 1.6,
					}}
				>
					Автоматизируйте контроль вылова, отслеживайте квоты в реальном времени
					и упростите отчётность для вашей рыболовной компании
				</Typography>

				{/* Кнопки */}
				<Box
					mt={4}
					sx={{
						display: 'flex',
						gap: 2,
						justifyContent: 'center',
						flexWrap: 'wrap',
					}}
				>
					<Button
						variant='contained'
						size='large'
						onClick={() => navigate('/register')}
						sx={{
							px: 4,
							py: 1.5,
							fontSize: '1.1rem',
							fontWeight: 600,
							borderRadius: 2,
						}}
					>
						Начать работу
					</Button>
					{/* <Button
						variant='outlined'
						size='large'
						onClick={() => navigate('/about')}
						sx={{
							px: 4,
							py: 1.5,
							fontSize: '1.1rem',
							fontWeight: 600,
							borderRadius: 2,
						}}
					>
						Узнать больше
					</Button> */}
				</Box>
			</Container>
		</Box>
	)
}

export default HeroSection
