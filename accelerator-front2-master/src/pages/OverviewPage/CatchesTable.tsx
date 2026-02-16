import React, { useState } from 'react'
import {
	Paper,
	Table,
	TableBody,
	TableCell,
	TableContainer,
	TableHead,
	TableRow,
	TablePagination,
	Box,
	Typography,
	Chip,
	IconButton,
	Tooltip,
} from '@mui/material'
import { Visibility, Download } from '@mui/icons-material'

// Mock данные уловов
const catchesData = [
	{
		id: 1,
		date: '2024-01-15',
		fisherman: 'Иванов А.П.',
		species: 'Хамса',
		weight: 1250.5,
		region: 'Азовское море',
		status: 'confirmed',
		quotaUsed: 88,
	},
	{
		id: 2,
		date: '2024-01-14',
		fisherman: 'Петров С.М.',
		species: 'Тюлька',
		weight: 850.2,
		region: 'Азовское море',
		status: 'confirmed',
		quotaUsed: 75,
	},
	{
		id: 3,
		date: '2024-01-13',
		fisherman: 'Сидоров В.К.',
		species: 'Кефаль',
		weight: 320.7,
		region: 'Чёрное море',
		status: 'pending',
		quotaUsed: 96,
	},
	{
		id: 4,
		date: '2024-01-12',
		fisherman: 'Козлов Д.И.',
		species: 'Камбала-калкан',
		weight: 150.3,
		region: 'Чёрное море',
		status: 'confirmed',
		quotaUsed: 75,
	},
	{
		id: 5,
		date: '2024-01-11',
		fisherman: 'Иванов А.П.',
		species: 'Хамса',
		weight: 980.1,
		region: 'Азовское море',
		status: 'confirmed',
		quotaUsed: 88,
	},
]

const CatchesTable: React.FC = () => {
	const [page, setPage] = useState(0)
	const [rowsPerPage, setRowsPerPage] = useState(10)

	const handleChangePage = (_event: unknown, newPage: number) => {
		setPage(newPage)
	}

	const handleChangeRowsPerPage = (
		event: React.ChangeEvent<HTMLInputElement>
	) => {
		setRowsPerPage(parseInt(event.target.value, 10))
		setPage(0)
	}

	const getStatusColor = (status: string) => {
		switch (status) {
			case 'confirmed':
				return 'success'
			case 'pending':
				return 'warning'
			case 'rejected':
				return 'error'
			default:
				return 'default'
		}
	}

	const getStatusText = (status: string) => {
		switch (status) {
			case 'confirmed':
				return 'Подтверждён'
			case 'pending':
				return 'На проверке'
			case 'rejected':
				return 'Отклонён'
			default:
				return status
		}
	}

	const getQuotaColor = (percentage: number) => {
		if (percentage >= 90) return 'error'
		if (percentage >= 75) return 'warning'
		return 'primary'
	}

	return (
		<Paper sx={{ width: '100%', overflow: 'hidden' }}>
			<Box
				sx={{
					p: 2,
					display: 'flex',
					justifyContent: 'space-between',
					alignItems: 'center',
				}}
			>
				<Typography variant='h6' sx={{ fontWeight: 'bold' }}>
					Все уловы
				</Typography>
				<Tooltip title='Экспорт в Excel'>
					<IconButton>
						<Download />
					</IconButton>
				</Tooltip>
			</Box>

			<TableContainer>
				<Table stickyHeader>
					<TableHead>
						<TableRow>
							<TableCell>Дата</TableCell>
							<TableCell>Рыбак</TableCell>
							<TableCell>Вид рыбы</TableCell>
							<TableCell align='right'>Вес (кг)</TableCell>
							<TableCell>Район</TableCell>
							<TableCell>Статус</TableCell>
							<TableCell align='right'>% квоты</TableCell>
							<TableCell align='center'>Действия</TableCell>
						</TableRow>
					</TableHead>
					<TableBody>
						{catchesData
							.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
							.map(catchItem => (
								<TableRow hover key={catchItem.id}>
									<TableCell>{catchItem.date}</TableCell>
									<TableCell sx={{ fontWeight: 'medium' }}>
										{catchItem.fisherman}
									</TableCell>
									<TableCell>{catchItem.species}</TableCell>
									<TableCell align='right'>
										{catchItem.weight.toLocaleString()}
									</TableCell>
									<TableCell>{catchItem.region}</TableCell>
									<TableCell>
										<Chip
											label={getStatusText(catchItem.status)}
											color={getStatusColor(catchItem.status)}
											size='small'
										/>
									</TableCell>
									<TableCell align='right'>
										<Chip
											label={`${catchItem.quotaUsed}%`}
											color={getQuotaColor(catchItem.quotaUsed)}
											variant='outlined'
											size='small'
										/>
									</TableCell>
									<TableCell align='center'>
										<Tooltip title='Просмотр деталей'>
											<IconButton size='small'>
												<Visibility />
											</IconButton>
										</Tooltip>
									</TableCell>
								</TableRow>
							))}
					</TableBody>
				</Table>
			</TableContainer>

			<TablePagination
				rowsPerPageOptions={[5, 10, 25]}
				component='div'
				count={catchesData.length}
				rowsPerPage={rowsPerPage}
				page={page}
				onPageChange={handleChangePage}
				onRowsPerPageChange={handleChangeRowsPerPage}
				labelRowsPerPage='Строк на странице:'
				labelDisplayedRows={({ from, to, count }) =>
					`${from}-${to} из ${count}`
				}
			/>
		</Paper>
	)
}

export default CatchesTable
