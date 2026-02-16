import React from 'react'
import { Container, Typography, Box } from '@mui/material'
import OverviewFilters from './OverviewFilters'
import CatchesTable from './CatchesTable'
import StatisticsCards from './StatisticsCards'
import QuotasProgress from './QuotasProgress'
import StatisticsCardsAllTime from './StatisticsCardsAllTime'

const OverviewPage: React.FC = () => {
	return (
		<Container maxWidth='xl'>
			<Box sx={{ py: 4 }}>
				{/* Заголовок */}
				<Typography
					variant='h4'
					component='h1'
					gutterBottom
					sx={{ fontWeight: 'bold' }}
				>
					📊 Обзор уловов
				</Typography>
				<Typography variant='body1' color='text.secondary' sx={{ mb: 4 }}>
					Полная статистика и аналитика по всем уловам в системе
				</Typography>

				<Box sx={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
					{/* Фильтры */}
					{/* <OverviewFilters /> */}

					{/* Статистические карточки */}
					<StatisticsCards />

					<Typography variant='h6' sx={{ fontWeight: 'bold' }}>
						Статистика за все время
					</Typography>
					<StatisticsCardsAllTime />

					{/* Прогресс квот */}
					<QuotasProgress />

					{/* Таблица уловов */}
					{/* <CatchesTable /> */}
				</Box>
			</Box>
		</Container>
	)
}

export default OverviewPage
