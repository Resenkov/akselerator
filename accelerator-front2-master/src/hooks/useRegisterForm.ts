import { useState } from 'react'

export interface RegisterFormData {
	companyName: string
	legalName: string
	inn: string
	ogrn: string
	email: string
	phone: string
	website: string
	address: string
	city: string
	region: string
	postalCode: string
	country: string
	username: string
	password: string
	confirmPassword: string
	fullName: string
	position: string
	companyType: string
	fleetSize: string
	fishingAreas: string[]
	annualVolume: string
	acceptTerms: boolean
}

export const useRegisterForm = () => {
	const [activeStep, setActiveStep] = useState(2)
	const [isLoading, setIsLoading] = useState(false)
	const [error, setError] = useState('')
	const [formData, setFormData] = useState<RegisterFormData>({
		companyName: '',
		legalName: '',
		inn: '',
		ogrn: '',
		email: '',
		phone: '',
		website: '',
		address: '',
		city: '',
		region: '',
		postalCode: '',
		country: 'Россия',
		username: '',
		password: '',
		confirmPassword: '',
		fullName: '',
		position: '',
		companyType: '',
		fleetSize: '',
		fishingAreas: [],
		annualVolume: '',
		acceptTerms: false,
	})

	const handleInputChange =
		(field: keyof RegisterFormData) =>
		(event: React.ChangeEvent<HTMLInputElement>) => {
			setFormData(prev => ({ ...prev, [field]: event.target.value }))
			if (error) setError('')
		}

	const handleCheckboxChange =
		(field: keyof RegisterFormData) =>
		(event: React.ChangeEvent<HTMLInputElement>) => {
			setFormData(prev => ({ ...prev, [field]: event.target.checked }))
		}

	const handleMultiSelectChange =
		(field: keyof RegisterFormData) =>
		(event: React.ChangeEvent<HTMLInputElement>) => {
			const value = event.target.value
			setFormData(prev => ({
				...prev,
				[field]: typeof value === 'string' ? value.split(',') : value,
			}))
		}

	const validateStep = (step: number): boolean => {
		switch (step) {
			case 0:
				if (
					!formData.companyName.trim() ||
					!formData.inn.trim() ||
					!formData.email.trim()
				) {
					setError('Заполните обязательные поля: Название компании, ИНН, Email')
					return false
				}
				if (formData.inn.length !== 10 && formData.inn.length !== 12) {
					setError('ИНН должен содержать 10 или 12 цифр')
					return false
				}
				break
			case 1:
				if (!formData.address.trim() || !formData.city.trim()) {
					setError('Заполните обязательные поля: Адрес, Город')
					return false
				}
				break
			case 2:
				if (
					!formData.username.trim() ||
					!formData.password ||
					!formData.fullName.trim() ||
					!formData.acceptTerms
				) {
					setError(
						'Заполните все обязательные поля и примите условия соглашения'
					)
					return false
				}
				if (formData.password.length < 8) {
					setError('Пароль должен содержать минимум 8 символов')
					return false
				}
				if (formData.password !== formData.confirmPassword) {
					setError('Пароли не совпадают')
					return false
				}
				break
		}
		return true
	}

	return {
		activeStep,
		formData,
		isLoading,
		error,
		handleInputChange,
		handleCheckboxChange,
		handleMultiSelectChange,
		validateStep,
		setActiveStep,
		setIsLoading,
		setError,
	}
}
