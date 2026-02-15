// CatchForm.tsx (обновленная версия)
import React, { useState } from 'react'
import {
	Box,
	TextField,
	MenuItem,
	Button,
	Typography,
	Alert,
	CircularProgress,
	Divider,
	Stack,
	Card,
	CardContent,
	useTheme,
	alpha,
} from '@mui/material'
import { DatePicker } from '@mui/x-date-pickers'
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider'
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns'
import { ru } from 'date-fns/locale'
import { Save, Clear, Assignment, Info } from '@mui/icons-material'
import useSWR, { useSWRConfig } from 'swr'
import { useSnackbar } from 'notistack'
import { api } from '../../api/api'
import { useAppSelector } from '../../hooks/storeHooks'

// Типы данных
interface CatchData {
	fishingDate: Date | null
	species: string
	weight: string
	region: string
	notes: string
}

interface Species {
	id: string
	scientificName: string
	commonName: string
	endangered: boolean
}

interface Region {
	id: string
	name: string
	code: string
}

const formatDateToYYYYMMDD = (date: Date) => {
	const year = date.getFullYear()
	const month = (date.getMonth() + 1).toString().padStart(2, '0')
	const day = date.getDate().toString().padStart(2, '0')
	return `${year}-${month}-${day}`
}

const CatchForm: React.FC = () => {
	const { enqueueSnackbar } = useSnackbar()
	const { mutate } = useSWRConfig()

	const userProfile = useAppSelector(state => state.userProfile)

	const theme = useTheme()
	const [formData, setFormData] = useState<CatchData>({
		fishingDate: new Date(),
		species: '',
		weight: '',
		region: '',
		notes: '',
	})
	const [isSubmitting, setIsSubmitting] = useState(false)
	const [submitSuccess, setSubmitSuccess] = useState(false)
	const [touched, setTouched] = useState({
		fishingDate: false,
		species: false,
		weight: false,
		region: false,
	})

	const [species, setSpecies] = useState<Species[]>([])
	const [regions, setRegions] = useState<Region[]>([])

	const handleInputChange =
		(field: keyof CatchData) =>
		(event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
			setFormData(prev => ({
				...prev,
				[field]: event.target.value,
			}))
			if (submitSuccess) setSubmitSuccess(false)
		}

	const handleDateChange = (date: Date | null) => {
		setFormData(prev => ({
			...prev,
			fishingDate: date,
		}))
		if (submitSuccess) setSubmitSuccess(false)
	}

	const handleBlur = (field: keyof typeof touched) => () => {
		setTouched(prev => ({
			...prev,
			[field]: true,
		}))
	}

	const handleSubmit = async (event: React.FormEvent) => {
		event.preventDefault()

		// Помечаем все поля как "тронутые" при отправке
		setTouched({
			fishingDate: true,
			species: true,
			weight: true,
			region: true,
		})

		// Проверяем валидность формы
		if (!isFormValid) return

		setIsSubmitting(true)

		try {
			const token = localStorage.getItem('token')

			await api.post(
				'/catch-reports',
				{
					organizationId: userProfile.organization.id,
					reportedBy: userProfile.user.id,
					regionId: Number(formData.region),
					speciesId: Number(formData.species),
					fishingDate: formData.fishingDate,
					weightKg: Number(formData.weight).toFixed(1),
					notes: formData.notes,
					verified: false,
				},
				{
					headers: {
						Authorization: token ? `Bearer ${token}` : '',
					},
				}
			)

			enqueueSnackbar('Улов успешно сохранен', { variant: 'success' })

			mutate('/catch-reports/my/last3/table')
			mutate('/my/quotas/allocation')

			setSubmitSuccess(true)
			handleClear()
		} catch (error) {
			console.log(error)
			enqueueSnackbar(
				'Произошла ошибка при установлении квоты: ' +
					error.response.data.message,
				{ variant: 'error' }
			)
		} finally {
			setIsSubmitting(false)
		}
	}

	const handleClear = () => {
		setFormData({
			fishingDate: new Date(),
			species: '',
			weight: '',
			region: '',
			notes: '',
		})
		setTouched({
			fishingDate: false,
			species: false,
			weight: false,
			region: false,
		})
	}

	// Валидационные функции
	const getDateError = () => touched.fishingDate && !formData.fishingDate
	const getSpeciesError = () => touched.species && !formData.species
	const getWeightError = () =>
		touched.weight && (!formData.weight || parseFloat(formData.weight) <= 0)
	const getRegionError = () => touched.region && !formData.region

	const isFormValid =
		formData.fishingDate &&
		formData.species &&
		formData.weight &&
		formData.region &&
		parseFloat(formData.weight) > 0

	const fetcher = async (url: string) => {
		const token = localStorage.getItem('token')
		await api
			.get(url, {
				headers: {
					Authorization: token ? `Bearer ${token}` : '',
				},
			})
			.then(res => {
				setSpecies(res.data.species)
				setRegions(res.data.regions)
				return res.data
			})
	}

	const { data, isLoading, error } = useSWR(
		'/catch-reports/my/form-meta',
		fetcher
	)

	return (
		<LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={ru}>
			<Box
				sx={{
					borderRadius: 2,
					mx: 'auto',
				}}
			>
				<Box component='form' onSubmit={handleSubmit}>
					{/* Заголовок */}
					<Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
						<Box
							sx={{
								display: 'flex',
								alignItems: 'center',
								justifyContent: 'center',
								width: 48,
								height: 48,
								backgroundColor: theme.palette.primary.main,
								borderRadius: 1,
							}}
						>
							<Assignment sx={{ color: 'white', fontSize: 24 }} />
						</Box>
						<Box>
							<Typography variant='h5' sx={{ fontWeight: 600 }}>
								Регистрация улова
							</Typography>
							<Typography variant='body2' color='text.secondary'>
								Заполните информацию о вылове рыбы
							</Typography>
						</Box>
					</Box>

					{submitSuccess && (
						<Alert
							severity='success'
							sx={{
								mb: 3,
								border: `1px solid ${theme.palette.success.light}`,
								backgroundColor: alpha(theme.palette.success.light, 0.1),
							}}
						>
							Успешно! Улов записан в систему.
						</Alert>
					)}

					{/* Основные поля в колонку */}
					<Stack spacing={3}>
						{/* Первая строка полей */}
						<Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
							<Box sx={{ flex: 1, minWidth: 250 }}>
								<DatePicker
									label='Дата вылова *'
									value={formData.fishingDate}
									onChange={handleDateChange}
									onClose={() => handleBlur('fishingDate')()}
									slotProps={{
										textField: {
											fullWidth: true,
											required: true,
											error: getDateError(),
											helperText: getDateError() ? 'Выберите дату вылова' : '',
											onBlur: handleBlur('fishingDate'),
										},
									}}
								/>
							</Box>

							<Box sx={{ flex: 1, minWidth: 250 }}>
								<TextField
									select
									label='Вид рыбы *'
									value={formData.species}
									onChange={handleInputChange('species')}
									onBlur={handleBlur('species')}
									fullWidth
									required
									error={getSpeciesError()}
									helperText={getSpeciesError() ? 'Выберите вид рыбы' : ''}
								>
									{species.map(spec => (
										<MenuItem key={spec.id} value={spec.id}>
											<Box>
												<Typography variant='body1'>
													{spec.commonName}
												</Typography>
												<Typography variant='caption' color='text.secondary'>
													{spec.scientificName}
												</Typography>
											</Box>
										</MenuItem>
									))}
								</TextField>
							</Box>
						</Box>

						{/* Вторая строка полей */}
						<Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
							<Box sx={{ flex: 1, minWidth: 250 }}>
								<TextField
									label='Вес улова (кг) *'
									type='number'
									value={formData.weight}
									onChange={handleInputChange('weight')}
									onBlur={handleBlur('weight')}
									fullWidth
									required
									error={getWeightError()}
									helperText={getWeightError() ? 'Введите вес больше 0' : ''}
									inputProps={{
										min: '0.001',
										step: '0.001',
										max: '10000',
									}}
									placeholder='0.000'
								/>
							</Box>

							<Box sx={{ flex: 1, minWidth: 250 }}>
								<TextField
									select
									label='Район вылова *'
									value={formData.region}
									onChange={handleInputChange('region')}
									onBlur={handleBlur('region')}
									fullWidth
									required
									error={getRegionError()}
									helperText={getRegionError() ? 'Выберите район вылова' : ''}
								>
									{regions.map(region => (
										<MenuItem key={region.id} value={region.id}>
											<Box>
												<Typography variant='body1'>{region.name}</Typography>
												<Typography variant='caption' color='text.secondary'>
													{region.code}
												</Typography>
											</Box>
										</MenuItem>
									))}
								</TextField>
							</Box>
						</Box>

						{/* Примечания */}
						<Card
							variant='outlined'
							sx={{ backgroundColor: alpha(theme.palette.primary.light, 0.02) }}
						>
							<CardContent sx={{ p: 3 }}>
								<Box
									sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}
								>
									<Info color='primary' fontSize='small' />
									<Typography variant='subtitle2' color='primary'>
										Дополнительная информация
									</Typography>
								</Box>
								<TextField
									label='Примечания'
									value={formData.notes}
									onChange={handleInputChange('notes')}
									fullWidth
									multiline
									rows={3}
									placeholder='Дополнительная информация об улове, условиях, оборудовании...'
									variant='outlined'
								/>
							</CardContent>
						</Card>
					</Stack>

					<Divider sx={{ my: 4 }} />

					{/* Кнопки действий */}
					<Box
						sx={{
							display: 'flex',
							gap: 2,
							justifyContent: 'flex-end',
							flexWrap: 'wrap',
						}}
					>
						<Button
							variant='outlined'
							onClick={handleClear}
							startIcon={<Clear />}
							disabled={isSubmitting}
							size='large'
							sx={{ minWidth: 120 }}
						>
							Очистить
						</Button>
						<Button
							type='submit'
							variant='contained'
							disabled={!isFormValid || isSubmitting}
							startIcon={
								isSubmitting ? <CircularProgress size={20} /> : <Save />
							}
							size='large'
							sx={{
								minWidth: 140,
								boxShadow: 'none',
								'&:hover': {
									boxShadow: 'none',
								},
							}}
						>
							{isSubmitting ? 'Отправка...' : 'Сохранить'}
						</Button>
					</Box>

					{/* Подсказка по валидации */}
					{!isFormValid && Object.values(touched).some(Boolean) && (
						<Alert
							severity='warning'
							icon={<Info />}
							sx={{
								mt: 3,
								backgroundColor: alpha(theme.palette.warning.light, 0.1),
							}}
						>
							Пожалуйста, заполните все обязательные поля правильно
						</Alert>
					)}
				</Box>
			</Box>
		</LocalizationProvider>
	)
}

export default CatchForm
