import React from 'react'
import {
	Paper,
	Typography,
	Box,
	Stack,
	useTheme,
	CircularProgress,
	Alert,
} from '@mui/material'
import { CalendarToday, Scale, TrendingUp, Place } from '@mui/icons-material'
import useSwr from 'swr'
import { api } from '../../api/api'
import { useAppSelector } from '../../hooks/storeHooks'

interface StatsData {
	organizationId: number
	totalCatches: number
	totalWeightKg: number
	catchesThisMonth: number
	mostFrequentRegionId: number
	mostFrequentRegionName: string
}

const CatchSummary: React.FC = () => {
	const theme = useTheme()

	const userProfile = useAppSelector(state => state.userProfile)

	const fetcher = async (url: string): Promise<StatsData> => {
		const token = localStorage.getItem('token')
		const response = await api.get(url, {
			headers: {
				Authorization: token ? `Bearer ${token}` : '',
			},
		})
		return response.data
	}

	const { data, isLoading, error } = useSwr(
		`/catch-reports/organization/${userProfile.organization.id}/stats`,
		fetcher
	)

	// Показываем заглушку при загрузке
	if (isLoading) {
		return (
			<Paper elevation={3} sx={{ p: 4, textAlign: 'center' }}>
				<CircularProgress size={40} />
				<Typography variant='body1' sx={{ mt: 2 }}>
					Загрузка статистики...
				</Typography>
			</Paper>
		)
	}

	// Показываем ошибку
	if (error) {
		return (
			<Paper elevation={3} sx={{ p: 4 }}>
				<Alert severity='error' sx={{ mb: 2 }}>
					Ошибка при загрузке статистики
				</Alert>
				<Typography variant='body2' color='text.secondary'>
					Не удалось загрузить данные. Попробуйте обновить страницу.
				</Typography>
			</Paper>
		)
	}

	// Используем данные из API или заглушку если данных нет
	const stats = data || {
		organizationId: 1,
		totalCatches: 0,
		totalWeightKg: 0,
		catchesThisMonth: 0,
		mostFrequentRegionId: 1,
		mostFrequentRegionName: 'Азовское море',
	}

	return (
		<Paper elevation={3} sx={{ p: 4 }}>
			{/* Заголовок */}
			<Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
				<Box
					sx={{
						display: 'flex',
						alignItems: 'center',
						justifyContent: 'center',
						width: 40,
						height: 40,
						backgroundColor: theme.palette.primary.main,
						borderRadius: 1,
					}}
				>
					<TrendingUp sx={{ color: 'white', fontSize: 20 }} />
				</Box>
				<Box>
					<Typography variant='h6' sx={{ fontWeight: 600 }}>
						Статистика уловов
					</Typography>
					<Typography variant='body2' color='text.secondary'>
						Обзор за всё время
					</Typography>
				</Box>
			</Box>

			<Stack spacing={3}>
				{/* Основные метрики */}
				<Box
					sx={{
						display: 'flex',
						gap: 3,
						flexWrap: 'wrap',
						justifyContent: 'space-between',
					}}
				>
					<Stack direction='column' alignItems='center'>
						<Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 1 }}>
							<CalendarToday color='primary' fontSize='small' />
							<Typography variant='body2' color='text.secondary'>
								Всего уловов
							</Typography>
						</Box>
						<Typography variant='h4' sx={{ fontWeight: 700 }}>
							{stats.totalCatches}
						</Typography>
					</Stack>

					<Stack direction='column' alignItems='center'>
						<Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 1 }}>
							<Scale color='primary' fontSize='small' />
							<Typography variant='body2' color='text.secondary'>
								Общий вес
							</Typography>
						</Box>
						<Typography variant='h4' sx={{ fontWeight: 700 }}>
							{stats.totalWeightKg.toLocaleString()} кг
						</Typography>
					</Stack>

					<Stack direction='column' alignItems='center'>
						<Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 1 }}>
							<TrendingUp color='primary' fontSize='small' />
							<Typography variant='body2' color='text.secondary'>
								В этом месяце
							</Typography>
						</Box>
						<Typography variant='h4' sx={{ fontWeight: 700 }}>
							{stats.catchesThisMonth}
						</Typography>
					</Stack>
				</Box>

				{/* Дополнительная информация */}
				<Box
					sx={{
						pt: 3,
						borderTop: `1px solid ${theme.palette.divider}`,
						display: 'flex',
						justifyContent: 'space-between',
						alignItems: 'center',
						flexWrap: 'wrap',
						gap: 2,
					}}
				>
					<Box>
						<Typography variant='body2' color='text.secondary' gutterBottom>
							Основной регион:
						</Typography>
						<Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
							<Place fontSize='small' color='action' />
							<Typography variant='body1' sx={{ fontWeight: 600 }}>
								{stats.mostFrequentRegionName || 'Вы еще не ловили рыбу'}
							</Typography>
						</Box>
					</Box>
				</Box>
			</Stack>
		</Paper>
	)
}

export default CatchSummary
