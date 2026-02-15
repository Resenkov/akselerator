import React from 'react'
import { Box, TextField, MenuItem } from '@mui/material'
import { LocationOn } from '@mui/icons-material'
import { type RegisterFormData } from '../../../hooks/useRegisterForm'

interface AddressStepProps {
	formData: RegisterFormData
	onInputChange: (
		field: keyof RegisterFormData
	) => (event: React.ChangeEvent<HTMLInputElement>) => void
	onMultiSelectChange: (
		field: keyof RegisterFormData
	) => (event: React.ChangeEvent<HTMLInputElement>) => void
}

const fishingAreasOptions = [
	'Азовское море',
	'Чёрное море',
	'Баренцево море',
	'Балтийское море',
	'Каспийское море',
	'Охотское море',
	'Японское море',
	'Белое море',
	'Реки и озёра',
	'Дальневосточный бассейн',
]

const fleetSizes = [
	'1-5 судов',
	'6-10 судов',
	'11-20 судов',
	'21-50 судов',
	'Более 50 судов',
]

const annualVolumes = [
	'До 100 тонн',
	'100-500 тонн',
	'500-1000 тонн',
	'1000-5000 тонн',
	'Более 5000 тонн',
]

const AddressStep: React.FC<AddressStepProps> = ({
	formData,
	onInputChange,
	onMultiSelectChange,
}) => {
	return (
		<Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
			<TextField
				label='Юридический адрес *'
				value={formData.address}
				onChange={onInputChange('address')}
				required
				fullWidth
				sx={{ minWidth: 300 }}
				InputProps={{
					startAdornment: (
						<LocationOn sx={{ color: 'text.secondary', mr: 1 }} />
					),
				}}
				placeholder='ул. Примерная, д. 1'
			/>

			<Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
				<TextField
					label='Город *'
					value={formData.city}
					onChange={onInputChange('city')}
					required
					sx={{ minWidth: 150, flex: 1 }}
					placeholder='Москва'
				/>
				<TextField
					label='Регион'
					value={formData.region}
					onChange={onInputChange('region')}
					sx={{ minWidth: 150, flex: 1 }}
					placeholder='Московская область'
				/>
				<TextField
					label='Почтовый индекс'
					value={formData.postalCode}
					onChange={onInputChange('postalCode')}
					sx={{ minWidth: 120, flex: 1 }}
					placeholder='123456'
				/>
			</Box>

			<Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
				<TextField
					select
					label='Размер флота'
					value={formData.fleetSize}
					onChange={onInputChange('fleetSize')}
					sx={{ minWidth: 200, flex: 1 }}
				>
					<MenuItem value=''>Не указано</MenuItem>
					{fleetSizes.map(size => (
						<MenuItem key={size} value={size}>
							{size}
						</MenuItem>
					))}
				</TextField>
				<TextField
					select
					label='Годовой объем вылова'
					value={formData.annualVolume}
					onChange={onInputChange('annualVolume')}
					sx={{ minWidth: 200, flex: 1 }}
				>
					<MenuItem value=''>Не указано</MenuItem>
					{annualVolumes.map(volume => (
						<MenuItem key={volume} value={volume}>
							{volume}
						</MenuItem>
					))}
				</TextField>
			</Box>

			<TextField
				select
				label='Районы промысла'
				value={formData.fishingAreas}
				onChange={onMultiSelectChange('fishingAreas')}
				fullWidth
				sx={{ minWidth: 250 }}
				SelectProps={{
					multiple: true,
					renderValue: selected => (selected as string[]).join(', '),
				}}
			>
				{fishingAreasOptions.map(area => (
					<MenuItem key={area} value={area}>
						{area}
					</MenuItem>
				))}
			</TextField>
		</Box>
	)
}

export default AddressStep
