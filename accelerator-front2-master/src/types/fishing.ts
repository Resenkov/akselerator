export interface FishSpecies {
	id: number
	scientific_name: string
	common_name: string
	is_endangered: boolean
}

export interface FishingRegion {
	id: number
	code: string
	name: string
}

export interface Organization {
	id: number
	name: string
	org_type: 'COMPANY' | 'GOVERNMENT'
	inn?: string
	region_id: number
}

export interface User {
	id: number
	organization_id: number | null
	username: string
	email: string
	roles: string[]
	organization?: Organization
}

export interface CatchReport {
	id: number
	organization_id: number
	reported_by: number
	species_id: number
	region_id: number
	fishing_date: string
	weight_kg: number
	notes?: string
	is_verified: boolean
	created_at: string
}

export interface Quota {
	id: number
	organization_id: number
	species_id: number
	region_id: number
	period_start: string
	period_end: string
	limit_kg: number
	used_kg?: number
	percentage?: number
}
