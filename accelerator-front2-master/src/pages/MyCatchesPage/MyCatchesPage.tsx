import React from 'react'
import { Typography, Box, Button } from '@mui/material'
import { Add } from '@mui/icons-material'
import { useNavigate } from 'react-router-dom'
// import MyCatchesFilters from './MyCatchesFilters'
import MyCatchesTable from './MyCatchesTable'
import CatchSummary from './CatchSummary'

const MyCatchesPage: React.FC = () => {
	const navigate = useNavigate()

	return (
		<Box sx={{ py: 4 }}>
			{/* Заголовок с кнопками действий */}
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
						variant='h4'
						component='h1'
						gutterBottom
						sx={{ fontWeight: 'bold' }}
					>
						Мои уловы
					</Typography>
					<Typography variant='body1' color='text.secondary'>
						История всех ваших уловов и текущая статистика
					</Typography>
				</Box>
				<Box sx={{ display: 'flex', gap: 2 }}>
					{/* <Button variant='outlined' startIcon={<Download />}>
						Экспорт
					</Button> */}
					<Button
						variant='contained'
						startIcon={<Add />}
						onClick={() => navigate('/catch')}
					>
						Новый улов
					</Button>
				</Box>
			</Box>

			<Box sx={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
				{/* Сводка по уловам */}
				<CatchSummary />

				{/* Фильтры */}
				{/* <MyCatchesFilters /> */}

				{/* Таблица уловов */}
				<MyCatchesTable />
			</Box>
		</Box>
	)
}

export default MyCatchesPage
