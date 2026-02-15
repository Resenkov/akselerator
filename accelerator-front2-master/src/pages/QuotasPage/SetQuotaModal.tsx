import React, { useState } from 'react'
import {
	Modal,
	Box,
	TextField,
	MenuItem,
	Button,
	Typography,
	IconButton,
} from '@mui/material'
import { DatePicker } from '@mui/x-date-pickers/DatePicker'
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider'
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns'
import { ru } from 'date-fns/locale'
import { Close, Save } from '@mui/icons-material'
import useSWR, { useSWRConfig } from 'swr'
import { api } from '../../api/api'
import { useSnackbar } from 'notistack'
import type { FishSpecies, FishingRegion } from '../../interfaces'

interface SetQuotaModalProps {
	open: boolean
	onClose: () => void
	title?: string
}

interface QuotaFormData {
	species: string
	region: string
	startDate: Date | null
	endDate: Date | null
	limit: string
}

// Основной интерфейс для данных
export interface FishingData {
	species: FishSpecies[]
	regions: FishingRegion[]
}

const SetQuotaModal: React.FC<SetQuotaModalProps> = ({
	open,
	onClose,
	title,
}) => {
	const { enqueueSnackbar } = useSnackbar()
	const { mutate } = useSWRConfig()

	const [formData, setFormData] = useState<QuotaFormData>({
		species: '',
		region: '',
		startDate: new Date(),
		endDate: new Date(new Date().setFullYear(new Date().getFullYear() + 1)),
		limit: '',
	})

	const [species, setSpecies] = useState<FishSpecies[]>([])
	const [regions, setRegions] = useState<FishingRegion[]>([])

	const handleInputChange =
		(field: keyof QuotaFormData) =>
		(event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
			setFormData(prev => ({ ...prev, [field]: event.target.value }))
		}

	const handleStartDateChange = (date: Date | null) => {
		setFormData(prev => ({
			...prev,
			startDate: date,
			// Автоматически корректируем endDate, если он становится раньше startDate
			endDate:
				prev.endDate && date && prev.endDate < date ? date : prev.endDate,
		}))
	}

	const handleEndDateChange = (date: Date | null) => {
		setFormData(prev => ({ ...prev, endDate: date }))
	}

	//    *   "regionId": 1,
	//  *   "periodStart": "2025-01-01",
	//  *   "periodEnd": "2025-12-31",
	//  *   "limitKg": 100000.000
	const handleSubmit = async (event: React.FormEvent) => {
		event.preventDefault()

		try {
			const token = localStorage.getItem('token')

			await api.post(
				'/region-total-quotas',
				{
					regionId: Number(formData.region),
					periodStart: formData.startDate,
					periodEnd: formData.endDate,
					limitKg: Number(formData.limit),
					speciesId: Number(formData.species),
				},
				{
					headers: {
						Authorization: token ? `Bearer ${token}` : '',
					},
				}
			)

			enqueueSnackbar('Квота успешно установлена', { variant: 'success' })

			mutate('/region-total-quotas')

			onClose()
			setFormData({
				species: '',
				region: '',
				startDate: new Date(),
				endDate: new Date(new Date().setFullYear(new Date().getFullYear() + 1)),
				limit: '',
			})
		} catch (error) {
			console.log(error)
			enqueueSnackbar(
				'Произошла ошибка при установлении квоты: ' +
					error.response.data.message,
				{ variant: 'error' }
			)
		}
	}

	const handleClose = () => {
		onClose()
		setFormData({
			species: '',
			region: '',
			startDate: new Date(),
			endDate: new Date(new Date().setFullYear(new Date().getFullYear() + 1)),
			limit: '',
		})
	}

	const isFormValid =
		formData.species &&
		formData.region &&
		formData.startDate &&
		formData.endDate &&
		formData.startDate <= formData.endDate &&
		formData.limit &&
		parseFloat(formData.limit) > 0

	const fetcher = async url => {
		const token = localStorage.getItem('token')

		await api
			.get(url, {
				headers: {
					Authorization: token ? `Bearer ${token}` : '',
				},
			})
			.then(res => {
				const resData = res.data
				setRegions(resData.regions)
				setSpecies(resData.species)
				return resData
			})
	}
	const { data, isLoading, error } = useSWR(
		'/region-total-quotas/meta',
		fetcher
	)

	return (
		<LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={ru}>
			<Modal open={open} onClose={handleClose}>
				<Box
					sx={{
						position: 'absolute',
						top: '50%',
						left: '50%',
						transform: 'translate(-50%, -50%)',
						width: { xs: '90%', sm: 580 },
						bgcolor: 'background.paper',
						boxShadow: 24,
						p: 4,
						borderRadius: 2,
					}}
				>
					{/* Заголовок */}
					<Box
						sx={{
							display: 'flex',
							justifyContent: 'space-between',
							alignItems: 'center',
							mb: 3,
						}}
					>
						<Typography variant='h5' sx={{ fontWeight: 'bold' }}>
							{title || 'Установить новую квоту'}
						</Typography>
						<IconButton onClick={handleClose} size='small'>
							<Close />
						</IconButton>
					</Box>

					{/* Форма */}
					<Box component='form' onSubmit={handleSubmit}>
						<Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
							<TextField
								select
								label='Вид рыбы'
								value={formData.species}
								onChange={handleInputChange('species')}
								required
								fullWidth
							>
								{species.map(sp => (
									<MenuItem key={sp.id} value={sp.id}>
										{sp.commonName}
									</MenuItem>
								))}
							</TextField>

							<TextField
								select
								label='Район вылова'
								value={formData.region}
								onChange={handleInputChange('region')}
								required
								fullWidth
							>
								{regions.map(region => (
									<MenuItem key={region.id} value={region.id}>
										{region.name}
									</MenuItem>
								))}
							</TextField>

							<Box sx={{ display: 'flex', gap: 2 }}>
								<Box sx={{ width: '50%' }}>
									<DatePicker
										label='Дата начала'
										value={formData.startDate}
										onChange={handleStartDateChange}
										slotProps={{
											textField: {
												fullWidth: true,
												required: true,
												error:
													formData.startDate &&
													formData.endDate &&
													formData.startDate > formData.endDate,
												helperText:
													formData.startDate &&
													formData.endDate &&
													formData.startDate > formData.endDate
														? 'Дата начала не может быть позже даты окончания'
														: '',
											},
										}}
									/>
								</Box>

								<Box sx={{ width: '50%' }}>
									<DatePicker
										label='Дата окончания'
										value={formData.endDate}
										onChange={handleEndDateChange}
										minDate={formData.startDate}
										slotProps={{
											textField: {
												fullWidth: true,
												required: true,
												error:
													formData.startDate &&
													formData.endDate &&
													formData.startDate > formData.endDate,
												helperText:
													formData.startDate &&
													formData.endDate &&
													formData.startDate > formData.endDate
														? 'Дата окончания не может быть раньше даты начала'
														: '',
											},
										}}
									/>
								</Box>
							</Box>

							<TextField
								label='Лимит (кг)'
								type='number'
								value={formData.limit}
								onChange={handleInputChange('limit')}
								required
								fullWidth
								inputProps={{ min: '1', step: '1' }}
								helperText='Введите лимит в килограммах'
							/>
						</Box>

						{/* Кнопки действий */}
						<Box
							sx={{
								display: 'flex',
								gap: 2,
								justifyContent: 'flex-end',
								mt: 4,
							}}
						>
							<Button variant='outlined' onClick={handleClose}>
								Отмена
							</Button>
							<Button
								type='submit'
								variant='contained'
								disabled={!isFormValid}
								startIcon={<Save />}
							>
								Сохранить квоту
							</Button>
						</Box>
					</Box>
				</Box>
			</Modal>
		</LocalizationProvider>
	)
}

export default SetQuotaModal
