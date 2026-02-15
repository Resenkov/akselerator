import React, { useState } from 'react'
import {
	Paper,
	Typography,
	Box,
	LinearProgress,
	Chip,
	Alert,
	FormControl,
	Select,
	MenuItem,
	type SelectChangeEvent,
	TextField,
	InputAdornment,
	Tabs,
	Tab,
	Button,
} from '@mui/material'
import { Warning, CalendarToday, Search, Refresh } from '@mui/icons-material'
import { api } from '../../api/api'
import useSWR from 'swr'

interface QuotaItem {
	id: number
	organizationId: number
	organizationName: string
	speciesId: number
	speciesCommonName: string
	speciesScientificName: string
	regionId: number
	regionName: string
	regionCode: string
	periodStart: string
	periodEnd: string
	limitKg: number
	usedKg: number
}

interface QuotasResponse {
	content: QuotaItem[]
	pageable: any
	totalPages: number
	totalElements: number
	last: boolean
	numberOfElements: number
	size: number
	number: number
	sort: any
	first: boolean
	empty: boolean
}

interface ProcessedQuota {
	species: string
	region: string
	used: number
	total: number
	percentage: number
	status: 'normal' | 'warning' | 'critical'
	regionCode: string
	speciesScientificName: string
	organizationName: string
	period: string
}

const QuotasProgress: React.FC = () => {
	// Состояние для выбранного года
	const [selectedYear, setSelectedYear] = useState<string>(
		new Date().getFullYear().toString()
	)
	const [customYear, setCustomYear] = useState<string>('')
	const [searchMode, setSearchMode] = useState<'quick' | 'custom'>('quick')

	// Создаем список ближайших лет (5 лет назад + текущий + 5 лет вперед)
	const currentYear = new Date().getFullYear()
	const generateQuickYears = () => {
		const years: number[] = []
		// 5 лет назад
		for (let i = 5; i > 0; i--) {
			years.push(currentYear - i)
		}
		// Текущий год
		years.push(currentYear)
		// 5 лет вперед
		for (let i = 1; i <= 5; i++) {
			years.push(currentYear + i)
		}
		return years.map(y => y.toString())
	}

	const availableYears = generateQuickYears()

	const fetcher = async (url: string) => {
		const token = localStorage.getItem('token')
		const response = await api.get(url, {
			headers: {
				Authorization: token ? `Bearer ${token}` : '',
			},
		})
		return response.data
	}

	const { data, isLoading, error, mutate } = useSWR<QuotasResponse>(
		`/allocation-quotas?year=${selectedYear}`,
		fetcher,
		{
			revalidateOnFocus: false,
		}
	)

	// Обработчик быстрого выбора года
	const handleQuickYearChange = (event: SelectChangeEvent) => {
		setSelectedYear(event.target.value)
		setSearchMode('quick')
	}

	// Обработчик ввода произвольного года
	const handleCustomYearChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		const value = e.target.value
		// Разрешаем только цифры и максимум 4 символа
		if (/^\d*$/.test(value) && value.length <= 4) {
			setCustomYear(value)
		}
	}

	// Применить произвольный год
	const handleApplyCustomYear = () => {
		if (customYear && /^\d{4}$/.test(customYear)) {
			const yearNum = parseInt(customYear, 10)
			if (yearNum >= 2000 && yearNum <= 2100) {
				setSelectedYear(customYear)
				setSearchMode('quick') // Переключаемся обратно на быстрый выбор
			}
		}
	}

	// Обработчик нажатия Enter в поле ввода
	const handleKeyPress = (e: React.KeyboardEvent) => {
		if (e.key === 'Enter') {
			handleApplyCustomYear()
		}
	}

	// Сбросить к текущему году
	const handleResetToCurrentYear = () => {
		const currentYearStr = currentYear.toString()
		setSelectedYear(currentYearStr)
		setSearchMode('quick')
		setCustomYear('')
	}

	// Функция для определения статуса квоты
	const getQuotaStatus = (
		percentage: number
	): 'normal' | 'warning' | 'critical' => {
		if (percentage >= 90) return 'critical'
		if (percentage >= 75) return 'warning'
		return 'normal'
	}

	// Обработка данных из API
	const getProcessedQuotas = (): ProcessedQuota[] => {
		if (!data?.content) return []

		// Группируем квоты по виду и региону
		const quotaMap = new Map<string, QuotaItem[]>()

		data.content.forEach(quota => {
			const key = `${quota.speciesCommonName}_${quota.regionName}`
			if (!quotaMap.has(key)) {
				quotaMap.set(key, [])
			}
			quotaMap.get(key)?.push(quota)
		})

		// Преобразуем сгруппированные данные
		const processedQuotas: ProcessedQuota[] = []

		quotaMap.forEach((quotas, key) => {
			// Суммируем лимиты и использованные значения
			const total = quotas.reduce((sum, q) => sum + q.limitKg, 0)
			const used = quotas.reduce((sum, q) => sum + q.usedKg, 0)
			const percentage = total > 0 ? Math.round((used / total) * 100) : 0

			processedQuotas.push({
				species: quotas[0].speciesCommonName,
				region: quotas[0].regionName,
				used: Math.round(used),
				total: Math.round(total),
				percentage,
				status: getQuotaStatus(percentage),
				regionCode: quotas[0].regionCode,
				speciesScientificName: quotas[0].speciesScientificName,
				organizationName: quotas[0].organizationName,
				period: `${new Date(quotas[0].periodStart).toLocaleDateString(
					'ru-RU'
				)} - ${new Date(quotas[0].periodEnd).toLocaleDateString('ru-RU')}`,
			})
		})

		return processedQuotas.sort((a, b) => b.percentage - a.percentage)
	}

	// Функции для получения цвета и текста статуса
	const getStatusColor = (status: string) => {
		switch (status) {
			case 'critical':
				return 'error'
			case 'warning':
				return 'warning'
			default:
				return 'primary'
		}
	}

	const getStatusText = (status: string) => {
		switch (status) {
			case 'critical':
				return 'Критично'
			case 'warning':
				return 'Предупреждение'
			default:
				return 'Норма'
		}
	}

	const processedQuotas = getProcessedQuotas()
	const criticalQuotas = processedQuotas.filter(q => q.status === 'critical')

	// Общее использование квот
	const totalUsage = processedQuotas.reduce((sum, q) => sum + q.used, 0)
	const totalLimit = processedQuotas.reduce((sum, q) => sum + q.total, 0)
	const overallPercentage =
		totalLimit > 0 ? Math.round((totalUsage / totalLimit) * 100) : 0

	// Проверка валидности введенного года
	const isValidYear = customYear.length === 4 && /^\d{4}$/.test(customYear)
	const yearNum = isValidYear ? parseInt(customYear, 10) : 0
	const isYearInValidRange = yearNum >= 2000 && yearNum <= 2100

	// Загрузка
	if (isLoading) {
		return (
			<Paper sx={{ p: 3 }}>
				<Box
					sx={{
						display: 'flex',
						justifyContent: 'space-between',
						alignItems: 'flex-start',
						mb: 3,
					}}
				>
					<Box>
						<Typography variant='h6' sx={{ fontWeight: 'bold' }}>
							Использование квот ({selectedYear} год)
						</Typography>
						<Button
							size='small'
							startIcon={<Refresh />}
							onClick={() => mutate()}
							disabled={isLoading}
							sx={{ mt: 1 }}
						>
							Обновить
						</Button>
					</Box>

					<Box sx={{ display: 'flex', gap: 2, alignItems: 'flex-start' }}>
						<Button
							size='small'
							variant={
								selectedYear === currentYear.toString()
									? 'contained'
									: 'outlined'
							}
							startIcon={<CalendarToday />}
							onClick={handleResetToCurrentYear}
						>
							{currentYear}
						</Button>

						<Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
							<Tabs
								value={searchMode}
								onChange={(_, value) => setSearchMode(value)}
								size='small'
								sx={{ minHeight: 'auto' }}
							>
								<Tab
									label='Быстрый'
									value='quick'
									sx={{ minHeight: 'auto', py: 0.5 }}
								/>
								<Tab
									label='Произвольный'
									value='custom'
									sx={{ minHeight: 'auto', py: 0.5 }}
								/>
							</Tabs>

							{searchMode === 'quick' ? (
								<FormControl size='small' sx={{ minWidth: 120 }}>
									<Select
										value={selectedYear}
										onChange={handleQuickYearChange}
										displayEmpty
									>
										{availableYears.map(year => (
											<MenuItem key={year} value={year}>
												{year}{' '}
												{year === currentYear.toString() ? '(текущий)' : ''}
											</MenuItem>
										))}
									</Select>
								</FormControl>
							) : (
								<Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
									<TextField
										size='small'
										value={customYear}
										onChange={handleCustomYearChange}
										onKeyPress={handleKeyPress}
										placeholder='ГГГГ'
										sx={{ width: 100 }}
										InputProps={{
											startAdornment: (
												<InputAdornment position='start'>
													<CalendarToday fontSize='small' />
												</InputAdornment>
											),
										}}
										error={!isValidYear || (isValidYear && !isYearInValidRange)}
										helperText={
											!isValidYear
												? 'Введите 4 цифры'
												: !isYearInValidRange
												? 'Год должен быть между 2000 и 2100'
												: ''
										}
									/>
									<Button
										// size='small'
										variant='contained'
										onClick={handleApplyCustomYear}
										disabled={!isValidYear || !isYearInValidRange}
										startIcon={<Search />}
										// sx={{ maxHeight: '40px' }}
									>
										ОК
									</Button>
								</Box>
							)}
						</Box>
					</Box>
				</Box>
				<Typography>Загрузка данных...</Typography>
			</Paper>
		)
	}

	// Ошибка
	if (error) {
		return (
			<Paper sx={{ p: 3 }}>
				<Box
					sx={{
						display: 'flex',
						justifyContent: 'space-between',
						alignItems: 'flex-start',
						mb: 3,
					}}
				>
					<Box>
						<Typography variant='h6' sx={{ fontWeight: 'bold' }}>
							Использование квот ({selectedYear} год)
						</Typography>
						<Button
							size='small'
							startIcon={<Refresh />}
							onClick={() => mutate()}
							sx={{ mt: 1 }}
						>
							Повторить
						</Button>
					</Box>

					<Box sx={{ display: 'flex', gap: 2, alignItems: 'flex-start' }}>
						<Button
							size='small'
							variant={
								selectedYear === currentYear.toString()
									? 'contained'
									: 'outlined'
							}
							startIcon={<CalendarToday />}
							onClick={handleResetToCurrentYear}
						>
							{currentYear}
						</Button>

						<Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
							<Tabs
								value={searchMode}
								onChange={(_, value) => setSearchMode(value)}
								size='small'
								sx={{ minHeight: 'auto' }}
							>
								<Tab
									label='Быстрый'
									value='quick'
									sx={{ minHeight: 'auto', py: 0.5 }}
								/>
								<Tab
									label='Произвольный'
									value='custom'
									sx={{ minHeight: 'auto', py: 0.5 }}
								/>
							</Tabs>

							{searchMode === 'quick' ? (
								<FormControl size='small' sx={{ minWidth: 120 }}>
									<Select
										value={selectedYear}
										onChange={handleQuickYearChange}
										displayEmpty
									>
										{availableYears.map(year => (
											<MenuItem key={year} value={year}>
												{year}{' '}
												{year === currentYear.toString() ? '(текущий)' : ''}
											</MenuItem>
										))}
									</Select>
								</FormControl>
							) : (
								<Box sx={{ display: 'flex', gap: 1 }}>
									<TextField
										size='small'
										value={customYear}
										onChange={handleCustomYearChange}
										onKeyPress={handleKeyPress}
										placeholder='ГГГГ'
										sx={{ width: 100 }}
										InputProps={{
											startAdornment: (
												<InputAdornment position='start'>
													<CalendarToday fontSize='small' />
												</InputAdornment>
											),
										}}
										error={!isValidYear || (isValidYear && !isYearInValidRange)}
										helperText={
											!isValidYear
												? 'Введите 4 цифры'
												: !isYearInValidRange
												? 'Год должен быть между 2000 и 2100'
												: ''
										}
									/>
									<Button
										// size='small'
										variant='contained'
										onClick={handleApplyCustomYear}
										disabled={!isValidYear || !isYearInValidRange}
										startIcon={<Search />}
										// sx={{ maxHeight: '40px' }}
									>
										ОК
									</Button>
								</Box>
							)}
						</Box>
					</Box>
				</Box>
				<Alert severity='error' sx={{ mt: 2 }}>
					Ошибка при загрузке данных квот. Пожалуйста, попробуйте позже.
				</Alert>
			</Paper>
		)
	}

	// Нет данных
	if (processedQuotas.length === 0) {
		return (
			<Paper sx={{ p: 3 }}>
				<Box
					sx={{
						display: 'flex',
						justifyContent: 'space-between',
						alignItems: 'flex-start',
						mb: 3,
					}}
				>
					<Box>
						<Typography variant='h6' sx={{ fontWeight: 'bold' }}>
							Использование квот ({selectedYear} год)
						</Typography>
						<Button
							size='small'
							startIcon={<Refresh />}
							onClick={() => mutate()}
							sx={{ mt: 1 }}
						>
							Обновить
						</Button>
					</Box>

					<Box sx={{ display: 'flex', gap: 2, alignItems: 'flex-start' }}>
						<Button
							size='small'
							variant={
								selectedYear === currentYear.toString()
									? 'contained'
									: 'outlined'
							}
							startIcon={<CalendarToday />}
							onClick={handleResetToCurrentYear}
						>
							{currentYear}
						</Button>

						<Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
							<Tabs
								value={searchMode}
								onChange={(_, value) => setSearchMode(value)}
								size='small'
								sx={{ minHeight: 'auto' }}
							>
								<Tab
									label='Быстрый'
									value='quick'
									sx={{ minHeight: 'auto', py: 0.5 }}
								/>
								<Tab
									label='Произвольный'
									value='custom'
									sx={{ minHeight: 'auto', py: 0.5 }}
								/>
							</Tabs>

							{searchMode === 'quick' ? (
								<FormControl size='small' sx={{ minWidth: 120 }}>
									<Select
										value={selectedYear}
										onChange={handleQuickYearChange}
										displayEmpty
									>
										{availableYears.map(year => (
											<MenuItem key={year} value={year}>
												{year}{' '}
												{year === currentYear.toString() ? '(текущий)' : ''}
											</MenuItem>
										))}
									</Select>
								</FormControl>
							) : (
								<Box sx={{ display: 'flex', gap: 1 }}>
									<TextField
										size='small'
										value={customYear}
										onChange={handleCustomYearChange}
										onKeyPress={handleKeyPress}
										placeholder='ГГГГ'
										sx={{ width: 100 }}
										InputProps={{
											startAdornment: (
												<InputAdornment position='start'>
													<CalendarToday fontSize='small' />
												</InputAdornment>
											),
										}}
										error={!isValidYear || (isValidYear && !isYearInValidRange)}
										helperText={
											!isValidYear
												? 'Введите 4 цифры'
												: !isYearInValidRange
												? 'Год должен быть между 2000 и 2100'
												: ''
										}
									/>
									<Button
										// size='small'
										variant='contained'
										onClick={handleApplyCustomYear}
										disabled={!isValidYear || !isYearInValidRange}
										startIcon={<Search />}
										// sx={{ maxHeight: '40px' }}
									>
										ОК
									</Button>
								</Box>
							)}
						</Box>
					</Box>
				</Box>
				<Alert severity='info' sx={{ mt: 2 }}>
					Нет данных о квотах на {selectedYear} год
				</Alert>
			</Paper>
		)
	}

	return (
		<Paper sx={{ p: 3 }}>
			<Box
				sx={{
					display: 'flex',
					justifyContent: 'space-between',
					alignItems: 'flex-start',
					mb: 3,
				}}
			>
				<Box>
					<Typography variant='h6' sx={{ fontWeight: 'bold' }}>
						Использование квот ({selectedYear} год)
					</Typography>
					<Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mt: 1 }}>
						<Typography variant='body2' color='text.secondary'>
							Всего: {totalUsage.toLocaleString()} /{' '}
							{totalLimit.toLocaleString()} кг ({overallPercentage}%)
						</Typography>
						<Button
							size='small'
							startIcon={<Refresh />}
							onClick={() => mutate()}
							disabled={isLoading}
						>
							Обновить
						</Button>
					</Box>
				</Box>

				<Box sx={{ display: 'flex', gap: 2, alignItems: 'flex-start' }}>
					<Button
						size='small'
						variant={
							selectedYear === currentYear.toString() ? 'contained' : 'outlined'
						}
						startIcon={<CalendarToday />}
						onClick={handleResetToCurrentYear}
					>
						{currentYear}
					</Button>

					<Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
						<Tabs
							value={searchMode}
							onChange={(_, value) => setSearchMode(value)}
							size='small'
							sx={{ minHeight: 'auto' }}
						>
							<Tab
								label='Быстрый'
								value='quick'
								sx={{ minHeight: 'auto', py: 0.5 }}
							/>
							<Tab
								label='Произвольный'
								value='custom'
								sx={{ minHeight: 'auto', py: 0.5 }}
							/>
						</Tabs>

						{searchMode === 'quick' ? (
							<FormControl size='small' sx={{ minWidth: 120 }}>
								<Select
									value={selectedYear}
									onChange={handleQuickYearChange}
									displayEmpty
								>
									{availableYears.map(year => (
										<MenuItem key={year} value={year}>
											{year}{' '}
											{year === currentYear.toString() ? '(текущий)' : ''}
										</MenuItem>
									))}
								</Select>
							</FormControl>
						) : (
							<Box>
								<TextField
									size='small'
									value={customYear}
									onChange={handleCustomYearChange}
									onKeyPress={handleKeyPress}
									placeholder='ГГГГ'
									sx={{ width: 100 }}
									InputProps={{
										startAdornment: (
											<InputAdornment position='start'>
												<CalendarToday fontSize='small' />
											</InputAdornment>
										),
									}}
									error={!isValidYear || (isValidYear && !isYearInValidRange)}
									helperText={
										!isValidYear
											? 'Введите 4 цифры'
											: !isYearInValidRange
											? 'Год должен быть между 2000 и 2100'
											: ''
									}
								/>
								<Button
									// size='small'
									variant='contained'
									onClick={handleApplyCustomYear}
									disabled={!isValidYear || !isYearInValidRange}
									startIcon={<Search />}
									sx={{ ml: 1 }}
								>
									ОК
								</Button>
							</Box>
						)}
					</Box>
				</Box>
			</Box>

			{criticalQuotas.length > 0 && (
				<Alert severity='warning' sx={{ mb: 3 }}>
					<strong>Внимание!</strong> {criticalQuotas.length} квот близки к
					исчерпанию
				</Alert>
			)}

			<Box
				sx={{
					display: 'flex',
					flexWrap: 'wrap',
					gap: 3,
				}}
			>
				{processedQuotas.map((quota, index) => (
					<Box
						key={index}
						sx={{
							width: { xs: '100%', md: 'calc(50% - 12px)' },
							mb: 2,
						}}
					>
						<Box sx={{ mb: 2 }}>
							<Box
								sx={{
									display: 'flex',
									justifyContent: 'space-between',
									alignItems: 'center',
									mb: 1,
								}}
							>
								<Box>
									<Typography variant='subtitle1' sx={{ fontWeight: 'bold' }}>
										{quota.species}
									</Typography>
									<Typography variant='caption' color='text.secondary'>
										{quota.speciesScientificName}
									</Typography>
								</Box>
								<Chip
									label={getStatusText(quota.status)}
									color={getStatusColor(quota.status)}
									size='small'
								/>
							</Box>

							<Typography variant='body2' color='text.secondary' gutterBottom>
								{quota.region} ({quota.regionCode})
							</Typography>

							<Typography
								variant='caption'
								color='text.secondary'
								display='block'
								gutterBottom
							>
								Период: {quota.period}
							</Typography>

							<Box
								sx={{
									display: 'flex',
									justifyContent: 'space-between',
									mb: 1,
								}}
							>
								<Typography variant='body2'>
									{quota.used.toLocaleString()} / {quota.total.toLocaleString()}{' '}
									кг
								</Typography>
								<Typography
									variant='body2'
									sx={{
										fontWeight: 'bold',
										color:
											quota.status === 'critical'
												? 'error.main'
												: quota.status === 'warning'
												? 'warning.main'
												: 'text.primary',
									}}
								>
									{quota.percentage}%
								</Typography>
							</Box>

							<LinearProgress
								variant='determinate'
								value={quota.percentage}
								color={getStatusColor(quota.status)}
								sx={{ height: 8, borderRadius: 4 }}
							/>

							<Box
								sx={{
									mt: 0.5,
									display: 'flex',
									justifyContent: 'space-between',
								}}
							>
								{quota.status === 'critical' && (
									<Typography variant='caption' color='error'>
										<Warning
											sx={{ fontSize: 12, verticalAlign: 'middle', mr: 0.5 }}
										/>
										Осталось {(quota.total - quota.used).toLocaleString()} кг
									</Typography>
								)}
								<Typography variant='caption' color='text.secondary'>
									Остаток: {(quota.total - quota.used).toLocaleString()} кг
								</Typography>
							</Box>
						</Box>
					</Box>
				))}
			</Box>

			{/* Статистика по статусам */}
			<Box sx={{ mt: 3, pt: 2, borderTop: 1, borderColor: 'divider' }}>
				<Typography variant='body2' color='text.secondary'>
					Всего квот: {processedQuotas.length} |
					<span style={{ color: 'green', marginLeft: '8px' }}>
						Норма: {processedQuotas.filter(q => q.status === 'normal').length}
					</span>
					<span style={{ color: 'orange', marginLeft: '8px' }}>
						Предупреждение:{' '}
						{processedQuotas.filter(q => q.status === 'warning').length}
					</span>
					<span style={{ color: 'red', marginLeft: '8px' }}>
						Критично:{' '}
						{processedQuotas.filter(q => q.status === 'critical').length}
					</span>
				</Typography>
			</Box>
		</Paper>
	)
}

export default QuotasProgress
