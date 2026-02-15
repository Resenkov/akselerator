/* eslint-disable @typescript-eslint/no-explicit-any */
import React from 'react'
import { MaterialReactTable } from 'material-react-table'
import { MRT_Localization_RU } from 'material-react-table/locales/ru'
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs'
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider'
import useSwr from 'swr'
import { api } from '../../api/api'
import { useAppSelector } from '../../hooks/storeHooks'

interface TableResponse {
	columns: Array<{
		header: string
		accessorKey: string
	}>
	data: any[]
}

const MyCatchesTable: React.FC = () => {
	const userProfile = useAppSelector(state => state.userProfile)

	const fetcher = async (url: string): Promise<TableResponse> => {
		const token = localStorage.getItem('token')
		const response = await api.get(url, {
			headers: {
				Authorization: token ? `Bearer ${token}` : '',
			},
		})
		return response.data
	}

	const { data, isLoading, error } = useSwr(
		`/catch-reports/organization/${userProfile.organization.id}/table`,
		fetcher
	)

	return (
		<LocalizationProvider dateAdapter={AdapterDayjs}>
			<MaterialReactTable
				columns={data?.columns || []}
				data={data?.data || []}
				localization={MRT_Localization_RU}
				state={{
					isLoading,
					showAlertBanner: !!error,
				}}
				muiToolbarAlertBannerProps={
					error
						? {
								color: 'error',
								children: 'Ошибка загрузки данных. ' + error.message,
						  }
						: undefined
				}
			/>
		</LocalizationProvider>
	)
}

export default MyCatchesTable
