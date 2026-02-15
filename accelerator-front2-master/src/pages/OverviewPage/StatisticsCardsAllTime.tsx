import React from 'react'
import { Card, CardContent, Typography, Box } from '@mui/material'
import { TrendingUp, Scale, Business, LocationOn } from '@mui/icons-material'
import { api } from '../../api/api'
import useSWR from 'swr'

// Тип для данных с сервера
interface StatisticsData {
	year: string
	totalCatchKg: number
	companiesCount: number
	regionsCount: number
	averageCatchKg: number
}

const StatisticsCardsAllTime: React.FC = () => {
	const fetcher = async (url: string) => {
		const token = localStorage.getItem('token')
		const response = await api.get(url, {
			headers: {
				Authorization: token ? `Bearer ${token}` : '',
			},
		})
		return response.data
	}

	const { data, isLoading, error } = useSWR<StatisticsData>(
		'/dashboard/cards/all',
		fetcher
	)

	// Форматирование чисел с разделителями тысяч
	const formatNumber = (num: number): string => {
		return num.toLocaleString('ru-RU', {
			maximumFractionDigits: 3,
			minimumFractionDigits: 0,
		})
	}

	// Конфигурация карточек на основе данных с сервера
	const getStatsFromData = (data: StatisticsData | undefined) => {
		if (!data) return []

		return [
			{
				title: 'Общий улов',
				value: formatNumber(data.totalCatchKg),
				unit: 'кг',
				icon: <Scale sx={{ fontSize: 40, color: 'primary.main' }} />,
			},
			{
				title: 'Компаний',
				value: data.companiesCount.toString(),
				unit: 'шт',
				icon: <Business sx={{ fontSize: 40, color: 'success.main' }} />,
			},
			{
				title: 'Районов вылова',
				value: data.regionsCount.toString(),
				unit: 'шт',
				icon: <LocationOn sx={{ fontSize: 40, color: 'info.main' }} />,
			},
			{
				title: 'Средний улов',
				value: formatNumber(data.averageCatchKg),
				unit: 'кг',
				icon: <TrendingUp sx={{ fontSize: 40, color: 'warning.main' }} />,
			},
		]
	}

	// Состояние загрузки
	if (isLoading) {
		return (
			<Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 3 }}>
				{[1, 2, 3, 4].map(index => (
					<Box
						key={index}
						sx={{
							width: {
								xs: '100%',
								sm: 'calc(50% - 12px)',
								md: 'calc(25% - 18px)',
							},
						}}
					>
						<Card sx={{ height: '100%' }}>
							<CardContent>
								<Box
									sx={{
										display: 'flex',
										justifyContent: 'space-between',
										alignItems: 'flex-start',
										mb: 2,
									}}
								>
									<Box sx={{ width: '100%' }}>
										<Box
											sx={{
												width: '60%',
												height: 24,
												bgcolor: 'grey.200',
												mb: 1,
												borderRadius: 1,
											}}
										/>
										<Box
											sx={{
												width: '40%',
												height: 32,
												bgcolor: 'grey.200',
												borderRadius: 1,
											}}
										/>
									</Box>
									<Box
										sx={{
											width: 40,
											height: 40,
											bgcolor: 'grey.200',
											borderRadius: 1,
										}}
									/>
								</Box>
							</CardContent>
						</Card>
					</Box>
				))}
			</Box>
		)
	}

	// Состояние ошибки
	if (error) {
		return (
			<Box
				sx={{
					display: 'flex',
					justifyContent: 'center',
					alignItems: 'center',
					p: 3,
				}}
			>
				<Typography color='error'>
					Ошибка при загрузке данных. Пожалуйста, попробуйте позже.
				</Typography>
			</Box>
		)
	}

	const stats = getStatsFromData(data)

	return (
		<Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 3 }}>
			{stats.map((stat, index) => (
				<Box
					key={index}
					sx={{
						width: {
							xs: '100%',
							sm: 'calc(50% - 12px)',
							md: 'calc(25% - 18px)',
						},
					}}
				>
					<Card sx={{ height: '100%' }}>
						<CardContent>
							<Box
								sx={{
									display: 'flex',
									justifyContent: 'space-between',
									alignItems: 'flex-start',
									mb: 2,
								}}
							>
								<Box>
									<Typography
										color='text.secondary'
										gutterBottom
										variant='overline'
									>
										{stat.title}
									</Typography>
									<Typography
										variant='h4'
										component='div'
										sx={{ fontWeight: 'bold' }}
									>
										{stat.value}
									</Typography>
									<Typography variant='body2' color='text.secondary'>
										{stat.unit}
									</Typography>
								</Box>
								{stat.icon}
							</Box>

							{/* Убраны секции с изменением за месяц и прогресс-баром, так как их нет в данных с сервера */}
						</CardContent>
					</Card>
				</Box>
			))}
		</Box>
	)
}

export default StatisticsCardsAllTime
