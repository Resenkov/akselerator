import React, { useState } from 'react'
import {
	Card,
	CardContent,
	Typography,
	Box,
	FormControl,
	Select,
	MenuItem,
	type SelectChangeEvent,
} from '@mui/material'
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

const StatisticsCards: React.FC = () => {
	// Состояние для выбранного года
	const currentYear = new Date().getFullYear()
	const [selectedYear, setSelectedYear] = useState<string>(
		currentYear.toString()
	)

	// Создаем список лет от 2004 до текущего
	const yearOptions = Array.from({ length: currentYear - 2004 + 1 }, (_, i) =>
		(currentYear - i).toString()
	).reverse()

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
		`/dashboard/cards?year=${selectedYear}`,
		fetcher
	)

	// Обработчик изменения года
	const handleYearChange = (event: SelectChangeEvent) => {
		setSelectedYear(event.target.value)
	}

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
			<Box>
				<Box
					sx={{
						display: 'flex',
						justifyContent: 'space-between',
						alignItems: 'center',
						mb: 3,
					}}
				>
					<Typography variant='h6' sx={{ fontWeight: 'bold' }}>
						Статистика за {selectedYear} год
					</Typography>
					<FormControl size='small' sx={{ minWidth: 120 }}>
						<Select
							value={selectedYear}
							onChange={handleYearChange}
							displayEmpty
							disabled={isLoading}
						>
							{yearOptions.map(year => (
								<MenuItem key={year} value={year}>
									{year} {year === currentYear.toString() ? '(текущий)' : ''}
								</MenuItem>
							))}
						</Select>
					</FormControl>
				</Box>
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
			</Box>
		)
	}

	// Состояние ошибки
	if (error) {
		return (
			<Box>
				<Box
					sx={{
						display: 'flex',
						justifyContent: 'space-between',
						alignItems: 'center',
						mb: 3,
					}}
				>
					<Typography variant='h6' sx={{ fontWeight: 'bold' }}>
						Статистика за {selectedYear} год
					</Typography>
					<FormControl size='small' sx={{ minWidth: 120 }}>
						<Select
							value={selectedYear}
							onChange={handleYearChange}
							displayEmpty
						>
							{yearOptions.map(year => (
								<MenuItem key={year} value={year}>
									{year} {year === currentYear.toString() ? '(текущий)' : ''}
								</MenuItem>
							))}
						</Select>
					</FormControl>
				</Box>
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
			</Box>
		)
	}

	const stats = getStatsFromData(data)

	return (
		<Box>
			<Box
				sx={{
					display: 'flex',
					justifyContent: 'space-between',
					alignItems: 'center',
					mb: 3,
				}}
			>
				<Typography variant='h6' sx={{ fontWeight: 'bold' }}>
					Статистика за {selectedYear} год
				</Typography>
				<FormControl size='small' sx={{ minWidth: 120 }}>
					<Select value={selectedYear} onChange={handleYearChange} displayEmpty>
						{yearOptions.map(year => (
							<MenuItem key={year} value={year}>
								{year} {year === currentYear.toString() ? '(текущий)' : ''}
							</MenuItem>
						))}
					</Select>
				</FormControl>
			</Box>

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
							</CardContent>
						</Card>
					</Box>
				))}
			</Box>
		</Box>
	)
}

export default StatisticsCards
