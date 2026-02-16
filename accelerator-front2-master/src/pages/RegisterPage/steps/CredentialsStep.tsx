import React from 'react'
import {
	Box,
	TextField,
	MenuItem,
	FormControlLabel,
	Checkbox,
	Typography,
} from '@mui/material'
import { Person, Lock, Badge } from '@mui/icons-material'
import { type RegisterFormData } from '../../../hooks/useRegisterForm'

interface CredentialsStepProps {
	formData: RegisterFormData
	onInputChange: (
		field: keyof RegisterFormData
	) => (event: React.ChangeEvent<HTMLInputElement>) => void
	onCheckboxChange: (
		field: keyof RegisterFormData
	) => (event: React.ChangeEvent<HTMLInputElement>) => void
}

const positions = [
	'Директор',
	'Менеджер',
	'Специалист по закупкам',
	'Логист',
	'Другое',
]

const CredentialsStep: React.FC<CredentialsStepProps> = ({
	formData,
	onInputChange,
	onCheckboxChange,
}) => {
	return (
		<Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
			<Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
				<TextField
					label='ФИО ответственного лица *'
					value={formData.fullName}
					onChange={onInputChange('fullName')}
					required
					sx={{ minWidth: 250, flex: 1 }}
					InputProps={{
						startAdornment: <Badge sx={{ color: 'text.secondary', mr: 1 }} />,
					}}
					placeholder='Иванов Иван Иванович'
				/>
				<TextField
					select
					label='Должность'
					value={formData.position}
					onChange={onInputChange('position')}
					sx={{ minWidth: 200, flex: 1 }}
				>
					<MenuItem value=''>Не выбрано</MenuItem>
					{positions.map(position => (
						<MenuItem key={position} value={position}>
							{position}
						</MenuItem>
					))}
				</TextField>
			</Box>

			<Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
				<TextField
					label='Имя пользователя *'
					value={formData.username}
					onChange={onInputChange('username')}
					required
					sx={{ minWidth: 200, flex: 1 }}
					InputProps={{
						startAdornment: <Person sx={{ color: 'text.secondary', mr: 1 }} />,
					}}
					placeholder='company_admin'
					helperText='Будет использоваться для входа в систему'
				/>
				<TextField
					label='Email для уведомлений'
					type='email'
					value={formData.email}
					onChange={onInputChange('email')}
					sx={{ minWidth: 250, flex: 1 }}
					placeholder='admin@company.ru'
				/>
			</Box>

			<Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
				<TextField
					label='Пароль *'
					type='password'
					value={formData.password}
					onChange={onInputChange('password')}
					required
					sx={{ minWidth: 200, flex: 1 }}
					InputProps={{
						startAdornment: <Lock sx={{ color: 'text.secondary', mr: 1 }} />,
					}}
					helperText='Минимум 8 символов'
				/>
				<TextField
					label='Подтверждение пароля *'
					type='password'
					value={formData.confirmPassword}
					onChange={onInputChange('confirmPassword')}
					required
					sx={{ minWidth: 200, flex: 1 }}
					InputProps={{
						startAdornment: <Lock sx={{ color: 'text.secondary', mr: 1 }} />,
					}}
				/>
			</Box>

			<FormControlLabel
				control={
					<Checkbox
						checked={formData.acceptTerms}
						onChange={onCheckboxChange('acceptTerms')}
						color='primary'
					/>
				}
				label={
					<Typography variant='body2'>
						Я принимаю условия пользовательского соглашения и даю согласие на
						обработку персональных данных
					</Typography>
				}
			/>

			<FormControlLabel
				control={<Checkbox color='primary' />}
				label={
					<Typography variant='body2'>
						Подписаться на рассылку новостей и обновлений системы
					</Typography>
				}
			/>
		</Box>
	)
}

export default CredentialsStep
