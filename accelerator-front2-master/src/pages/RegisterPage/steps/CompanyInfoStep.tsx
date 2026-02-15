import React from 'react'
import { Box, TextField, MenuItem } from '@mui/material'
import { Business, Email, Phone } from '@mui/icons-material'
import { type RegisterFormData } from '../../../hooks/useRegisterForm'

interface CompanyInfoStepProps {
	formData: RegisterFormData
	onInputChange: (
		field: keyof RegisterFormData
	) => (event: React.ChangeEvent<HTMLInputElement>) => void
}

const companyTypes = [
	'ИП',
	'ООО',
	'АО',
	'Сельхозпредприятие',
	'Рыболовецкий колхоз',
	'Другое',
]

const CompanyInfoStep: React.FC<CompanyInfoStepProps> = ({
	formData,
	onInputChange,
}) => {
	return (
		<Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
			<Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
				<TextField
					label='Название компании *'
					value={formData.companyName}
					onChange={onInputChange('companyName')}
					required
					sx={{ minWidth: 250, flex: 1 }}
					InputProps={{
						startAdornment: (
							<Business sx={{ color: 'text.secondary', mr: 1 }} />
						),
					}}
					placeholder='ООО "Рыбпром"'
				/>
				<TextField
					label='Юридическое название'
					value={formData.legalName}
					onChange={onInputChange('legalName')}
					sx={{ minWidth: 250, flex: 1 }}
					placeholder='Общество с ограниченной ответственностью "Рыбпром"'
				/>
			</Box>

			<Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
				<TextField
					label='ИНН *'
					value={formData.inn}
					onChange={onInputChange('inn')}
					required
					sx={{ minWidth: 200, flex: 1 }}
					inputProps={{ maxLength: 12 }}
					placeholder='1234567890'
					helperText='10 или 12 цифр'
				/>
				<TextField
					label='ОГРН'
					value={formData.ogrn}
					onChange={onInputChange('ogrn')}
					sx={{ minWidth: 200, flex: 1 }}
					placeholder='1234567890123'
				/>
			</Box>

			<Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
				<TextField
					select
					label='Организационно-правовая форма'
					value={formData.companyType}
					onChange={onInputChange('companyType')}
					sx={{ minWidth: 200, flex: 1 }}
				>
					<MenuItem value=''>Не выбрано</MenuItem>
					{companyTypes.map(type => (
						<MenuItem key={type} value={type}>
							{type}
						</MenuItem>
					))}
				</TextField>
				<TextField
					label='Веб-сайт'
					value={formData.website}
					onChange={onInputChange('website')}
					sx={{ minWidth: 250, flex: 1 }}
					placeholder='https://example.com'
				/>
			</Box>

			<Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
				<TextField
					label='Email компании *'
					type='email'
					value={formData.email}
					onChange={onInputChange('email')}
					required
					sx={{ minWidth: 250, flex: 1 }}
					InputProps={{
						startAdornment: <Email sx={{ color: 'text.secondary', mr: 1 }} />,
					}}
					placeholder='info@company.ru'
				/>
				<TextField
					label='Телефон компании'
					value={formData.phone}
					onChange={onInputChange('phone')}
					sx={{ minWidth: 200, flex: 1 }}
					InputProps={{
						startAdornment: <Phone sx={{ color: 'text.secondary', mr: 1 }} />,
					}}
					placeholder='+7 (999) 999-99-99'
				/>
			</Box>
		</Box>
	)
}

export default CompanyInfoStep
