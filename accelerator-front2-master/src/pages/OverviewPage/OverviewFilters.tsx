/* eslint-disable @typescript-eslint/no-explicit-any */
import React from 'react'
import {
	Paper,
	Box,
	TextField,
	MenuItem,
	Button,
	Typography,
	Chip,
} from '@mui/material'
import { FilterList, Clear } from '@mui/icons-material'
import { DatePicker } from '@mui/x-date-pickers'
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider'
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns'
import { ru } from 'date-fns/locale'

interface Filters {
	region: string
	species: string
	fisherman: string
	dateFrom: Date | null
	dateTo: Date | null
	status: string
}

const OverviewFilters: React.FC = () => {
	const [filters, setFilters] = React.useState<Filters>({
		region: '',
		species: '',
		fisherman: '',
		dateFrom: null,
		dateTo: null,
		status: '',
	})

	const regions = [
		{ id: '1', name: 'Азовское море' },
		{ id: '2', name: 'Чёрное море' },
		{ id: '3', name: 'Баренцево море' },
		{ id: '4', name: 'Балтийское море' },
	]

	const species = [
		{ id: '1', name: 'Хамса' },
		{ id: '2', name: 'Тюлька' },
		{ id: '3', name: 'Кефаль' },
		{ id: '4', name: 'Камбала-калкан' },
	]

	const handleFilterChange = (field: keyof Filters) => (value: any) => {
		setFilters(prev => ({ ...prev, [field]: value }))
	}

	const handleClearFilters = () => {
		setFilters({
			region: '',
			species: '',
			fisherman: '',
			dateFrom: null,
			dateTo: null,
			status: '',
		})
	}

	const activeFiltersCount = Object.values(filters).filter(
		value => value !== '' && value !== null
	).length

	return (
		<LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={ru}>
			<Paper sx={{ p: 3 }}>
				<Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 3 }}>
					<FilterList color='primary' />
					<Typography variant='h6' sx={{ fontWeight: 'bold' }}>
						Фильтры
					</Typography>
					{activeFiltersCount > 0 && (
						<Chip label={activeFiltersCount} color='primary' size='small' />
					)}
				</Box>

				<Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 3, mb: 3 }}>
					<Box sx={{ width: { xs: '100%', md: 'calc(25% - 18px)' } }}>
						<TextField
							select
							label='Район вылова'
							value={filters.region}
							onChange={e => handleFilterChange('region')(e.target.value)}
							fullWidth
						>
							<MenuItem value=''>Все районы</MenuItem>
							{regions.map(region => (
								<MenuItem key={region.id} value={region.id}>
									{region.name}
								</MenuItem>
							))}
						</TextField>
					</Box>

					<Box sx={{ width: { xs: '100%', md: 'calc(25% - 18px)' } }}>
						<TextField
							select
							label='Вид рыбы'
							value={filters.species}
							onChange={e => handleFilterChange('species')(e.target.value)}
							fullWidth
						>
							<MenuItem value=''>Все виды</MenuItem>
							{species.map(sp => (
								<MenuItem key={sp.id} value={sp.id}>
									{sp.name}
								</MenuItem>
							))}
						</TextField>
					</Box>

					<Box sx={{ width: { xs: '100%', md: 'calc(25% - 18px)' } }}>
						<DatePicker
							label='Дата от'
							value={filters.dateFrom}
							onChange={handleFilterChange('dateFrom')}
							slotProps={{ textField: { fullWidth: true } }}
						/>
					</Box>

					<Box sx={{ width: { xs: '100%', md: 'calc(25% - 18px)' } }}>
						<DatePicker
							label='Дата до'
							value={filters.dateTo}
							onChange={handleFilterChange('dateTo')}
							slotProps={{ textField: { fullWidth: true } }}
						/>
					</Box>
				</Box>

				<Box
					sx={{
						display: 'flex',
						gap: 2,
						justifyContent: 'flex-start',
						flexWrap: 'wrap',
					}}
				>
					<Button
						variant='contained'
						startIcon={<FilterList />}
						onClick={() => console.log('Применить фильтры:', filters)}
					>
						Применить фильтры
					</Button>
					<Button
						variant='outlined'
						startIcon={<Clear />}
						onClick={handleClearFilters}
					>
						Очистить
					</Button>
				</Box>
			</Paper>
		</LocalizationProvider>
	)
}

export default OverviewFilters
