export interface IUserData {
	token: string | null
	tokenType: string
	user: {
		id: number | null
		organizationId: number | null
		username: string
		email: string
		password: string | null
		active: boolean
	}
	organization: {
		id: number | null
		name: string
		orgType: string
		inn: string
		regionId: number | null
	}
	roles: string[]
	expired: boolean | null
	valid: boolean | null
	error: string | null
	message: string | null
}

// Интерфейс для вида рыбы
export interface FishSpecies {
	id: number
	scientificName: string // Научное название
	commonName: string // Обычное название
	endangered: boolean // Находится ли под угрозой исчезновения
}

// Интерфейс для региона
export interface FishingRegion {
	id: number
	code: string // Код региона (например, "AZOV")
	name: string // Название региона
}

// Интерфейс для организаций
export interface Company {
	id: number
	name: string
	orgType: string
	inn: string
}

// Интерфейс для сортировки
export interface Sort {
	unsorted: boolean
	sorted: boolean
	empty: boolean
}

// Интерфейс для пагинации
export interface Pageable {
	pageNumber: number
	pageSize: number
	sort: Sort
	offset: number
	paged: boolean
	unpaged: boolean
}

export interface FishingQuota {
	id: number
	organizationId: number
	organizationName: string
	speciesId: number
	speciesCommonName: string
	speciesScientificName: string
	regionId: number
	regionName: string
	regionCode: string
	periodStart: string // Дата в формате YYYY-MM-DD
	periodEnd: string // Дата в формате YYYY-MM-DD
	limitKg: number // Общая квота в кг
	usedKg: number
}

// Интерфейс для пагинированного ответа
export interface PageResponseQuotasCompany {
	content: FishingQuota[]
	pageable: Pageable
	totalPages: number
	totalElements: number
	last: boolean
	numberOfElements: number
	first: boolean
	size: number
	number: number
	sort: Sort
	empty: boolean
}
