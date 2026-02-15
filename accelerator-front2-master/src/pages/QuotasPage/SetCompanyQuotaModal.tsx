/* eslint-disable @typescript-eslint/no-explicit-any */
import React, { useState } from 'react'
import {
	Modal,
	Box,
	TextField,
	MenuItem,
	Button,
	Typography,
	IconButton,
	Autocomplete,
} from '@mui/material'
import { DatePicker } from '@mui/x-date-pickers/DatePicker'
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider'
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns'
import { ru } from 'date-fns/locale'
import { Close, Save } from '@mui/icons-material'
import useSWR, { useSWRConfig } from 'swr'
import { api } from '../../api/api'
import { useSnackbar } from 'notistack'
import type { FishSpecies, FishingRegion, Company } from '../../interfaces'

interface SetCompanyQuotaModalProps {
	open: boolean
	onClose: () => void
	title?: string
}

interface CompanyQuotaFormData {
	company: string
	species: string
	region: string
	startDate: Date | null
	endDate: Date | null
	limit: string
}

// Основной интерфейс для данных
export interface CompanyData {
	species: FishSpecies[]
	regions: FishingRegion[]
	organizations: Company[]
}

const SetCompanyQuotaModal: React.FC<SetCompanyQuotaModalProps> = ({
	open,
	onClose,
	title = 'Установить квоту компании',
}) => {
	const { enqueueSnackbar } = useSnackbar()
	const { mutate } = useSWRConfig()

	const [formData, setFormData] = useState<CompanyQuotaFormData>({
		company: '',
		species: '',
		region: '',
		startDate: new Date(),
		endDate: new Date(new Date().setFullYear(new Date().getFullYear() + 1)),
		limit: '',
	})

	const [loading, setLoading] = useState(false)

	const [companies, setCompanies] = useState<Company[]>([])
	const [species, setSpecies] = useState<Company[]>([])
	const [regions, setRegions] = useState<Company[]>([])

	const handleInputChange =
		(field: keyof CompanyQuotaFormData) =>
		(event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
			setFormData(prev => ({ ...prev, [field]: event.target.value }))
		}

	const handleCompanyChange = (_event: any, value: any) => {
		setFormData(prev => ({ ...prev, company: value?.id || '' }))
	}

	const handleStartDateChange = (date: Date | null) => {
		setFormData(prev => ({
			...prev,
			startDate: date,
			endDate:
				prev.endDate && date && prev.endDate < date ? date : prev.endDate,
		}))
	}

	const handleEndDateChange = (date: Date | null) => {
		setFormData(prev => ({ ...prev, endDate: date }))
	}

	const handleSubmit = async (event: React.FormEvent) => {
		event.preventDefault()
		setLoading(true)

		try {
			const token = localStorage.getItem('token')

			await api.post(
				'/my/quotas/allocation',
				{
					organizationId: formData.company,
					speciesId: formData.species,
					regionId: formData.region,
					periodStart: formData.startDate?.toISOString(),
					periodEnd: formData.endDate?.toISOString(),
					limitKg: Number(formData.limit),
				},
				{
					headers: {
						Authorization: token ? `Bearer ${token}` : '',
					},
				}
			)

			mutate('/allocation-quotas/table')

			enqueueSnackbar('Квота компании успешно установлена', {
				variant: 'success',
			})

			onClose()
			setFormData({
				company: '',
				species: '',
				region: '',
				startDate: new Date(),
				endDate: new Date(new Date().setFullYear(new Date().getFullYear() + 1)),
				limit: '',
			})
		} catch (error) {
			console.log(error)
			enqueueSnackbar(
				'Ошибка при установке квоты ' + error.response.data.message,
				{
					variant: 'error',
				}
			)
		} finally {
			setLoading(false)
		}
	}

	const handleClose = () => {
		onClose()
		setFormData({
			company: '',
			species: '',
			region: '',
			startDate: new Date(),
			endDate: new Date(new Date().setFullYear(new Date().getFullYear() + 1)),
			limit: '',
		})
	}

	const isFormValid =
		formData.company &&
		formData.species &&
		formData.region &&
		formData.startDate &&
		formData.endDate &&
		formData.startDate <= formData.endDate &&
		formData.limit &&
		parseFloat(formData.limit) > 0

	const selectedCompany = companies.find(c => c.id === formData.company)

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
				setCompanies(resData.organizations)

				return resData
			})
	}
	const { data, isLoading, error } = useSWR('/allocation-quotas/meta', fetcher)

	return (
		<LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={ru}>
			<Modal open={open} onClose={handleClose}>
				<Box
					sx={{
						position: 'absolute',
						top: '50%',
						left: '50%',
						transform: 'translate(-50%, -50%)',
						width: { xs: '90%', sm: 600 },
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
							{title}
						</Typography>
						<IconButton onClick={handleClose} size='small'>
							<Close />
						</IconButton>
					</Box>

					{/* Форма */}
					<Box component='form' onSubmit={handleSubmit}>
						<Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
							<Autocomplete
								options={companies}
								getOptionLabel={option => `${option.name} (ИНН: ${option.inn})`}
								value={selectedCompany || null}
								onChange={handleCompanyChange}
								renderInput={params => (
									<TextField
										{...params}
										label='Компания'
										required
										placeholder='Выберите компанию'
									/>
								)}
							/>

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
								helperText='Лимит для компании'
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
							<Button
								variant='outlined'
								onClick={handleClose}
								disabled={loading}
							>
								Отмена
							</Button>
							<Button
								type='submit'
								variant='contained'
								disabled={!isFormValid || loading}
								startIcon={<Save />}
							>
								{loading ? 'Сохранение...' : 'Сохранить квоту'}
							</Button>
						</Box>
					</Box>
				</Box>
			</Modal>
		</LocalizationProvider>
	)
}

export default SetCompanyQuotaModal
